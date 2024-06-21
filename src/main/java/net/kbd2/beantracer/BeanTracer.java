package net.kbd2.beantracer;

import net.kbd2.beantracer.raytracing.Camera;
import net.kbd2.beantracer.raytracing.Scene;
import net.kbd2.beantracer.raytracing.material.Dielectric;
import net.kbd2.beantracer.raytracing.material.Lambertian;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.material.Metal;
import net.kbd2.beantracer.raytracing.shape.Sphere;
import net.kbd2.beantracer.util.Colour;
import net.kbd2.beantracer.util.Point3;
import net.kbd2.beantracer.util.Util;

import java.io.IOException;

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
                        scene.add(new Sphere(centre, 0.2, sphereMaterial));
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

        camera.aspectRatio = 16.0 / 9.0;
        camera.imageWidth = 1200;
        camera.samplesPerPixel = 500;
        camera.maxDepth = 50;

        camera.vertFov = 20;
        camera.lookFrom = new Point3(13, 2, 3);
        camera.lookAt = new Point3(0, 0, 0);
        camera.upVector = new Point3(0, 1, 0);

        camera.dofAngle = 0.6;
        camera.focusDist = 10;

        camera.render(scene);
    }
}