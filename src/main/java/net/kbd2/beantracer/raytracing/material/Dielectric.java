package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

public class Dielectric extends Material {
    private final double refractionIndex;

    public Dielectric(double refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        double ri = hitData.frontFace ? (1.0 / refractionIndex) : refractionIndex;

        Vec3 unitDirection = in.dir().unit();
        double cosTheta = Math.min(unitDirection.mul(-1).dot(hitData.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);

        boolean cannotRefract = ri * sinTheta > 1.0;
        Vec3 direction;

        if (cannotRefract || reflectance(cosTheta, ri) > Math.random()) {
            direction = unitDirection.reflect(hitData.normal);
        } else {
            direction = unitDirection.refract(hitData.normal, ri);
        }

        return new ScatterData(new Ray(hitData.point, direction, in.time()), new Colour(1.0, 1.0, 1.0));
    }

    private static double reflectance(double cosine, double refractionIndex) {
        double r0 = (1 - refractionIndex) / (1 + refractionIndex);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }
}
