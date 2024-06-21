package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.util.Colour;
import net.kbd2.beantracer.util.Vec3;
import org.jetbrains.annotations.Nullable;

public class Metal extends Material {
    private final Colour albedo;
    private final double fuzz;

    public Metal(Colour albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = fuzz;
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        Vec3 reflected = in.dir().reflect(hitData.normal);
        reflected = reflected.unit().add(Vec3.randomUnitVector().mul(fuzz));
        if (reflected.dot(hitData.normal) > 0) {
            Ray scattered = new Ray(hitData.point, reflected);
            return new ScatterData(scattered, this.albedo);
        }
        else return null;
    }
}
