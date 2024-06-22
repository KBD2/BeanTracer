package net.kbd2.beantracer;

import net.kbd2.beantracer.raytracing.Camera;
import net.kbd2.beantracer.raytracing.Scene;
import net.kbd2.beantracer.raytracing.material.Dielectric;
import net.kbd2.beantracer.raytracing.material.Lambertian;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.material.Metal;
import net.kbd2.beantracer.raytracing.shape.Sphere;
import net.kbd2.beantracer.util.BVHNode;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.Util;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BeanTracer {

    public static void main(String[] args) throws IOException {
        Scene scene = new Scene();
        Camera camera = new Camera();

        Material groundMaterial = new Lambertian(new Colour(0.5, 0.5, 0.5));
        scene.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = Math.random();
                Point3 centre = new Point3(a + 0.9 * Math.random(), 0.2, b + 0.9 * Math.random());
                if (centre.sub(new Point3(4, 0.2, 0)).length() > 0.9) {
                    Material sphereMaterial;
                    if (chooseMat < 0.8) {
                        Colour albedo = new Colour(Colour.random().mul(Colour.random()));
                        sphereMaterial = new Lambertian(albedo);
                        Point3 centre2 = new Point3(centre.add(new Vec3(0, Util.rand(0, 0.5), 0)));
                        scene.add(new Sphere(centre, centre2, 0.2, sphereMaterial));
                    } else if (chooseMat < 0.95) {
                        Colour albedo = new Colour(Colour.random().mul(Colour.random()));
                        double fuzz = Util.rand(0, 0.5);
                        sphereMaterial = new Metal(albedo, fuzz);
                        scene.add(new Sphere(centre, 0.2, sphereMaterial));
                    } else {
                        sphereMaterial = new Dielectric(1.5);
                        scene.add(new Sphere(centre, 0.2, sphereMaterial));
                    }
                }
            }
        }

        Material material1 = new Dielectric(1.5);
        scene.add(new Sphere(new Point3(0, 1, 0), 1, material1));

        Material material2 = new Lambertian(new Colour(0.4, 0.2, 0.1));
        scene.add(new Sphere(new Point3(-4, 1, 0), 1, material2));

        Material material3 = new Metal(new Colour(0.7, 0.6, 0.5), 0.0);
        scene.add(new Sphere(new Point3(4, 1, 0), 1, material3));

        scene = new Scene(new BVHNode(scene));

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
}