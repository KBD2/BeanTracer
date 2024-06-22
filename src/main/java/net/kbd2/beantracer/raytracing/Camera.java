package net.kbd2.beantracer.raytracing;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.util.*;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;

public class Camera {
    public int imageWidth = 640;
    public int imageHeight = 480;

    public int threads = 4;

    public int samplesPerPixel = 100;
    public int maxDepth = 50;

    public double fov = 35;

    public Point3 lookFrom = new Point3(-2, 2, 1);
    public Point3 lookAt = new Point3(0, 0, -1);

    public double dofAngle = 10;
    public double focusDist = 3.4;

    private double pixelSamplesScale;

    private Point3 cameraPos;

    private Vec3 dofDiskU;
    private Vec3 dofDiskV;

    private Vec3 pixelDeltaU, pixelDeltaV;
    private Point3 upperLeftPixel;

    private static final Interval intensity = new Interval(0.000, 0.999);
    public static final Vec3 upVector = new Vec3(0, 1, 0);

    public void init() {
        // Camera backwards
        Vec3 w = lookFrom.sub(lookAt).unit();
        // Camera right
        Vec3 u = upVector.cross(w).unit();
        // Camera up
        Vec3 v = w.cross(u);

        double theta = Util.degToRad(fov);
        double h = Math.tan(theta / 2);
        double viewportWidth = 2.0 * h * focusDist;
        double viewportHeight = viewportWidth * ((double) (imageHeight) / imageWidth);

        Vec3 viewportU = u.mul(viewportWidth);
        Vec3 viewportV = v.mul(-viewportHeight);

        this.pixelDeltaU = viewportU.div(imageWidth);
        this.pixelDeltaV = viewportV.div(imageHeight);

        this.cameraPos = lookFrom;

        Point3 viewportUpperLeft = cameraPos.sub(w.mul(focusDist)).sub(viewportU.div(2)).sub(viewportV.div(2));
        this.upperLeftPixel = viewportUpperLeft.add(pixelDeltaU.add(pixelDeltaV).mul(0.5));

        double dofRadius = focusDist * Math.tan(Util.degToRad(dofAngle / 2));
        dofDiskU = u.mul(dofRadius);
        dofDiskV = v.mul(dofRadius);

        pixelSamplesScale = 1.0 / samplesPerPixel;
    }

    public void render(Scene scene) throws IOException {
        Map<Integer, ImageLineInt> lines = new ConcurrentSkipListMap<>();
        Queue<Integer> availableRows = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < imageHeight; i++) availableRows.add(i);

        ImageInfo imgInfo = new ImageInfo(imageWidth, imageHeight, 8, false);

        long start = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int thread = 0; thread < threads; thread++) {
            futures.add(CompletableFuture.runAsync(() -> {
                Integer y;
                while((y = availableRows.poll()) != null) {
                    System.out.println("Remaining rows: " + availableRows.size());
                    ImageLineInt imgLine = new ImageLineInt(imgInfo);
                    for (int x = 0; x < imgInfo.cols; x++) {
                        Colour computed = new Colour();

                        for (int sample = 0; sample < samplesPerPixel; sample++) {
                            Ray ray = getRay(x, y);
                            computed = computed.add(rayColour(scene, ray, maxDepth));
                        }

                        computed = new Colour(computed.mul(pixelSamplesScale));

                        double rRaw = linearToGamma(computed.r());
                        double gRaw = linearToGamma(computed.g());
                        double bRaw = linearToGamma(computed.b());

                        int r = (int) (256 * intensity.clamp(rRaw));
                        int g = (int) (256 * intensity.clamp(gRaw));
                        int b = (int) (256 * intensity.clamp(bRaw));
                        ImageLineHelper.setPixelRGB8(imgLine, x, r, g, b);
                    }
                    lines.put(y, imgLine);
                }
            }));
        }

        for (CompletableFuture<Void> future : futures) {
            future.join();
        }

        System.out.println("Render time: " + (System.currentTimeMillis() - start) / 1000.0 + "s");

        File file = new File("./out.png");
        if (file.exists()) {
            boolean ignored = file.delete();
        }
        boolean fileCreated = file.createNewFile();
        if (!fileCreated) {
            System.out.println("Couldn't create output file!\n");
            return;
        }
        PngWriter png = new PngWriter(file, imgInfo);


        for (int i = 0; i < imageHeight; i++) {
            png.writeRow(lines.get(i));
        }
        png.end();
    }

    private Colour rayColour(Scene scene, Ray ray, int depth) {
        if (depth <= 0) return new Colour(0, 0, 0);

        HitData hitData = scene.hit(ray, new Interval(0.001, Double.POSITIVE_INFINITY));
        if (hitData != null) {
            Material.ScatterData scatterData = hitData.mat.scatter(ray, hitData);
            if (scatterData != null) {
                return rayColour(scene, scatterData.ray(), depth - 1).mul(scatterData.attenuation());
            } else return new Colour(0, 0, 0);
        }

        Vec3 unitDir = ray.dir().unit();
        double a = 0.5 * (unitDir.y() + 1);
        return new Colour(1.0, 1.0, 1.0).lerp(new Colour(0.5, 0.7, 1.0), a);
    }

    private Ray getRay(int x, int y) {
        Vec3 offset = sampleSquare();
        Point3 pixelSample = upperLeftPixel
                .add(pixelDeltaU.mul(x + offset.x()))
                .add(pixelDeltaV.mul(y + offset.y()));
        Point3 rayOrigin = (this.dofAngle <= 0) ? cameraPos : dofDiskSample();
        Vec3 rayDirection = pixelSample.sub(rayOrigin);
        return new Ray(rayOrigin, rayDirection, ThreadLocalRandom.current().nextDouble());
    }

    private Vec3 sampleSquare() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Vec3(random.nextDouble() - 0.5, random.nextDouble() - 0.5, 0);
    }

    private Point3 dofDiskSample() {
        Vec3 p = Vec3.randomInUnitDisk();
        return cameraPos.add(dofDiskU.mul(p.x()).add(dofDiskV.mul(p.y())));
    }

    private double linearToGamma(double linearComponent) {
        if (linearComponent > 0) return Math.sqrt(linearComponent);
        return 0;
    }
}
