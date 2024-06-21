package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.util.Colour;
import net.kbd2.beantracer.util.Vec3;
import org.jetbrains.annotations.Nullable;

public class Lambertian extends Material {
    private final Colour albedo;

    public Lambertian(Colour albedo) {
        this.albedo = albedo;
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        Vec3 scatterDirection = hitData.normal.add(Vec3.randomUnitVector());
        if (scatterDirection.nearZero()) scatterDirection = hitData.normal;
        return new ScatterData(new Ray(hitData.point, scatterDirection), this.albedo);
    }
}
