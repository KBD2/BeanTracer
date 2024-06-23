package net.kbd2.beantracer;

import net.kbd2.beantracer.raytracing.Camera;
import net.kbd2.beantracer.raytracing.Scene;
import net.kbd2.beantracer.raytracing.material.*;
import net.kbd2.beantracer.raytracing.shape.Quad;
import net.kbd2.beantracer.raytracing.shape.Sphere;
import net.kbd2.beantracer.raytracing.texture.Checkered;
import net.kbd2.beantracer.raytracing.texture.Image;
import net.kbd2.beantracer.raytracing.texture.Noise;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.BVHNode;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.Util;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BeanTracer {
    private static Scene scene = new Scene();
    private static final Camera camera = new Camera();

    public static void main(String[] args) throws IOException {
        simpleLight();

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
        Scene world = new Scene();

        Texture checkered = new Checkered(0.32, new Colour(0.2, 0.3, 0.1), new Colour(0.9, 0.9, 0.9));
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

        scene = new Scene(new BVHNode(world));
    }

    private static void checkeredSpheres() {
        Texture checkered = new Checkered(0.32, new Colour(0.2, 0.3, 0.1), new Colour(0.9, 0.9, 0.9));
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
        Texture checkered = new Checkered(4, new Colour(0.2, 0.2, 0.2), new Colour(0.9, 0.9, 0.9));
        scene.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(checkered)));
        scene.add(new Sphere(new Point3(0, 2, 0), 2, new Lambertian(perlinTexture)));

        DiffuseLight light = new DiffuseLight(new Colour(1, 1, 10));
        scene.add(new Quad(new Point3(3, 1, -2), new Vec3(2, 0, 0), new Vec3(0, 2, 0), light));
        DiffuseLight sphereLight = new DiffuseLight(new Colour(4, 4, 4));
        scene.add(new Sphere(new Point3(0, 7, 0), 2, sphereLight));
    }
}