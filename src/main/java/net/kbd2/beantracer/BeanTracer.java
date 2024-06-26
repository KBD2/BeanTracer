package net.kbd2.beantracer;

import net.kbd2.beantracer.raytracing.Camera;
import net.kbd2.beantracer.raytracing.material.*;
import net.kbd2.beantracer.raytracing.shape.*;
import net.kbd2.beantracer.raytracing.texture.Checkered;
import net.kbd2.beantracer.raytracing.texture.Image;
import net.kbd2.beantracer.raytracing.texture.Noise;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.BVHNode;
import net.kbd2.beantracer.util.Util;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BeanTracer {
    private static HittableList scene = new HittableList();
    private static final Camera camera = new Camera();

    public static void main(String[] args) throws IOException {
        finalScene();

        JSONObject config = new JSONObject(Files.readString(Paths.get("./config.json")));

        JSONObject imageConfig = config.getJSONObject("image");
        JSONObject renderingConfig = config.getJSONObject("rendering");
        JSONObject cameraConfig = config.getJSONObject("camera");

        if (imageConfig != null) {
            camera.imageWidth = imageConfig.optInt("width", 640);
            camera.imageHeight = imageConfig.optInt("height", 480);
        }

        if (renderingConfig != null) {
            camera.threads = renderingConfig.optInt("threads", 4);
            camera.samplesPerPixel = renderingConfig.optInt("samples-per-pixel", 10);
            camera.maxDepth = renderingConfig.optInt("max-bounces", 10);
            JSONArray background = renderingConfig.optJSONArray("background", new JSONArray(new double[] {0, 0, 0}));
            camera.background = new Colour(background.getDouble(0), background.getDouble(1), background.getDouble(2));
        }

        if (cameraConfig != null) {
            camera.fov = cameraConfig.optDouble("fov", 35);
            camera.dofAngle = cameraConfig.optDouble("dof-angle", 0.6);
            camera.focusDist = cameraConfig.optDouble("focus-dist", 0);
            JSONArray lookFrom = cameraConfig.optJSONArray("pos", new JSONArray(new double[] {0, 0, 0}));
            camera.lookFrom = new Point3(lookFrom.getDouble(0), lookFrom.getDouble(1), lookFrom.getDouble(2));
            JSONArray lookAt = cameraConfig.optJSONArray("target", new JSONArray(new double[] {0, 0, -1}));
            camera.lookAt = new Point3(lookAt.getDouble(0), lookAt.getDouble(1), lookAt.getDouble(2));
        }

        camera.init();

        camera.render(scene);
    }

    private static void bouncingSpheres() {
        HittableList world = new HittableList();

        Texture checkered = new Checkered(0.32, new Colour(0.2, 0.3, 0.1), new Colour(0.9));
        Material groundMaterial = new Lambertian(checkered);
        world.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = Math.random();
                Point3 centre = new Point3(a + 0.9 * Math.random(), 0.2, b + 0.9 * Math.random());
                if (centre.sub(new Point3(4, 0.2, 0)).length() > 0.9) {
                    Material sphereMaterial;
                    if (chooseMat < 0.8) {
                        Colour albedo = new Colour(Colour.random().mul(Colour.random()));
                        sphereMaterial = new Lambertian(albedo);
                        if (Math.random() < 0.3) {
                            Point3 centre2 = new Point3(centre.add(new Vec3(0, Util.rand(0, 0.2), 0)));
                            world.add(new Sphere(centre, centre2, 0.2, sphereMaterial));
                        } else {
                            new Sphere(centre, 0.2, sphereMaterial);
                        }
                    } else if (chooseMat < 0.95) {
                        Colour albedo = new Colour(Colour.random().mul(Colour.random()));
                        double fuzz = Util.rand(0, 0.5);
                        sphereMaterial = new Metal(albedo, fuzz);
                        world.add(new Sphere(centre, 0.2, sphereMaterial));
                    } else {
                        sphereMaterial = new Dielectric(1.5);
                        world.add(new Sphere(centre, 0.2, sphereMaterial));
                    }
                }
            }
        }

        Image earth = new Image("./images/earthmap.png");
        Lambertian earthSurface = new Lambertian(earth);
        world.add(new Sphere(new Point3(4, 1, 0), 1, earthSurface));

        Material material3 = new Metal(new Colour(0.7, 0.6, 0.5), 0.0);
        world.add(new Sphere(new Point3(0, 1, 2), 1, material3));

        Material material1 = new Dielectric(1.5);
        world.add(new Sphere(new Point3(5, 1, 4), 1, material1));

        scene = new HittableList(new BVHNode(world));
    }

    private static void checkeredSpheres() {
        Texture checkered = new Checkered(0.32, new Colour(0.2, 0.3, 0.1), new Colour(0.9));
        scene.add(new Sphere(new Point3(0, -10, 0), 10, new Lambertian(checkered)));
        scene.add(new Sphere(new Point3(0, 10, 0), 10, new Lambertian(checkered)));
    }

    private static void earth() {
        Image earth = new Image("./images/earthmap.png");
        Lambertian earthSurface = new Lambertian(earth);
        Sphere globe = new Sphere(new Point3(0, 0, 0), 2, earthSurface);
        scene.add(globe);
    }

    private static void perlinSpheres() {
        Noise texture = new Noise(4);
        scene.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(texture)));
        scene.add(new Sphere(new Point3(0, 2, 0), 2, new Lambertian(texture)));
    }

    private static void quads() {
        Material left = new Lambertian(new Colour(1, 0.2, 0.2));
        Material back = new Lambertian(new Image("./images/earthmap.png"));
        Material right = new Dielectric(0.5);
        Material upper = new Metal(new Colour(0.2, 1, 0.2), 0.1);
        Material lower = new Lambertian(new Noise(4));

        scene.add(new Quad(new Point3(-3, -2, 5), new Vec3(0, 0, -4), new Vec3(0, 4, 0), left));
        scene.add(new Quad(new Point3(-2, -2, 0), new Vec3(4, 0, 0), new Vec3(0, 4, 0), back));
        scene.add(new Quad(new Point3(3, -2, 1), new Vec3(0, 0, 4), new Vec3(0, 4, 0), right));
        scene.add(new Quad(new Point3(-2, 3, 1), new Vec3(4, 0, 0), new Vec3(0, 0, 4), upper));
        scene.add(new Quad(new Point3(-2, -3, 5), new Vec3(4, 0, 0), new Vec3(0, 0, -4), lower));
    }

    private static void simpleLight() {
        Texture perlinTexture = new Noise(Noise.NoiseType.MARBLED, 4);
        Texture checkered = new Checkered(4, new Colour(0.2), new Colour(0.9));
        scene.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(checkered)));
        scene.add(new Sphere(new Point3(0, 2, 0), 1, new Dielectric(1.33)));
        scene.add(new Sphere(new Point3(0, 2, 0), 0.8, new Lambertian(new Image("./images/earthmap.png"))));

        DiffuseLight light = new DiffuseLight(new Colour(3.6, 3.1, 6));
        scene.add(new Quad(new Point3(3, 1, -2), new Vec3(2, 0, 0), new Vec3(0, 2, 0), light));
        DiffuseLight sphereLight = new DiffuseLight(new Colour(10));
        scene.add(new Sphere(new Point3(0, 7, 0), 2, sphereLight));

        Hittable box = Quad.box(new Point3(0, 0, 4), new Point3(1, 2, 5), new Lambertian(perlinTexture));
        box = new RotateY(box, 15);
        scene.add(box);
    }

    private static void cornellBox() {
        Material red = new Lambertian(new Colour(0.65, 0.05, 0.05));
        Material white = new Lambertian(new Colour(0.73));
        Material green = new Lambertian(new Colour(0.12, 0.45, 0.15));
        Material light = new DiffuseLight(new Colour(15));

        scene.add(new Quad(new Point3(555, 0, 0), new Vec3(0, 555, 0), new Vec3(0, 0, 555), green));
        scene.add(new Quad(new Point3(0, 0, 0), new Vec3(0, 555, 0), new Vec3(0, 0, 555), red));
        scene.add(new Quad(new Point3(343, 554, 332), new Vec3(-130, 0, 0), new Vec3(0, 0, -105), light));
        scene.add(new Quad(new Point3(0, 0, 0), new Vec3(555, 0, 0), new Vec3(0, 0, 555), white));
        scene.add(new Quad(new Point3(555, 555, 555), new Vec3(-555, 0, 0), new Vec3(0, 0, -555), white));
        scene.add(new Quad(new Point3(0, 0, 555), new Vec3(555, 0, 0), new Vec3(0, 555, 0), white));

        Hittable box1 = Quad.box(new Point3(0, 0, 0), new Point3(165, 330, 165), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vec3(265, 0, 295));
        scene.add(box1);

        /*Hittable box2 = Quad.box(new Point3(0, 0, 0), new Point3(165, 165, 165), white);
        box2 = new RotateY(box2, -18);
        box2 = new Translate(box2, new Vec3(130, 0, 65));
        scene.add(box2);*/

        scene.add(new Sphere(new Point3(215, 150, 140), 80, new Dielectric(1.33)));
    }

    private static void cornellSmoke() {
        Material red = new Lambertian(new Colour(0.65, 0.05, 0.05));
        Material white = new Lambertian(new Colour(0.73));
        Material green = new Lambertian(new Colour(0.12, 0.45, 0.15));
        Material light = new DiffuseLight(new Colour(7));

        scene.add(new Quad(new Point3(555, 0, 0), new Vec3(0, 555, 0), new Vec3(0, 0, 555), green));
        scene.add(new Quad(new Point3(0, 0, 0), new Vec3(0, 555, 0), new Vec3(0, 0, 555), red));
        scene.add(new Quad(new Point3(343, 554, 332), new Vec3(-130, 0, 0), new Vec3(0, 0, -105), light));
        scene.add(new Quad(new Point3(0, 0, 0), new Vec3(555, 0, 0), new Vec3(0, 0, 555), white));
        scene.add(new Quad(new Point3(555, 555, 555), new Vec3(-555, 0, 0), new Vec3(0, 0, -555), white));
        scene.add(new Quad(new Point3(0, 0, 555), new Vec3(555, 0, 0), new Vec3(0, 555, 0), white));

        Hittable box1 = Quad.box(new Point3(0, 0, 0), new Point3(165, 330, 165), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vec3(265, 0, 295));
        scene.add(new ConstantMedium(box1, 0.01, new Colour(0)));

        Hittable box2 = Quad.box(new Point3(0, 0, 0), new Point3(165, 165, 165), white);
        box2 = new RotateY(box2, -18);
        box2 = new Translate(box2, new Vec3(130, 0, 65));
        scene.add(new ConstantMedium(box2, 0.01, (new Noise(Noise.NoiseType.MARBLED, 4))));
    }

    private static void finalScene() {
        HittableList groundBoxes =  new HittableList();
        Lambertian ground = new Lambertian(new Colour(0.48, 0.83, 0.53));
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                double w = 100;
                double x0 = -1000 + i * w;
                double z0 = -1000 + j * w;
                double y0 = 0;
                double x1 = x0 + w;
                double y1 = Util.rand(1, 101);
                double z1 = z0 + w;
                groundBoxes.add(Quad.box(new Point3(x0, y0, z0), new Point3(x1, y1, z1), ground));
            }
        }

        scene.add(new BVHNode(groundBoxes));

        Material light = new DiffuseLight(new Colour(7));
        scene.add(new Quad(new Point3(123, 554, 147), new Vec3(300, 0, 0), new Vec3(0, 0, 265), light));

        Point3 centre1 = new Point3(400, 400, 200);
        Point3 centre2 = centre1.add(new Vec3(30, 0, 0));
        Material sphereMaterial = new Lambertian(new Colour(0.7, 0.3, 0.1));
        scene.add(new Sphere(centre1, centre2, 50, sphereMaterial));

        scene.add(new Sphere(new Point3(260, 150, 45), 50, new Dielectric(1.5)));
        scene.add(new Sphere(new Point3(0, 150, 145), 50, new Metal(new Colour(0.8, 0.8, 0.9), 1)));

        Sphere boundary = new Sphere(new Point3(360, 150, 145), 70, new Dielectric(1.5));
        scene.add(boundary);
        scene.add(new ConstantMedium(boundary, 0.2, new Colour(0.2, 0.4, 0.9)));
        boundary = new Sphere(new Point3(0, 0, 0), 5000, new Dielectric(1.5));
        scene.add(new ConstantMedium(boundary, 0.0001, new Colour(1)));

        Lambertian eMat = new Lambertian(new Image("./images/earthmap.png"));
        scene.add(new Sphere(new Point3(400, 200, 400), 100, eMat));
        Texture perlin = new Noise(Noise.NoiseType.MARBLED, 0.2);
        scene.add(new Sphere(new Point3(220, 280, 300), 80, new Lambertian(perlin)));

        HittableList ballsBox = new HittableList();
        Material white = new Lambertian(new Colour(0.73));
        for (int j = 0; j < 1000; j++) {
            ballsBox.add(new Sphere(new Point3(Point3.random(0, 165)), 10, white));
        }
        scene.add(
                new Translate(
                        new RotateY(
                                new BVHNode(ballsBox),
                                15
                        ),
                        new Vec3(-100, 270, 395)
                )
        );
    }
}