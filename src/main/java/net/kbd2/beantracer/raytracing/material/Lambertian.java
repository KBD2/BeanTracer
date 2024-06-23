package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.texture.SolidColour;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

public class Lambertian extends Material {
    private final Texture texture;

    public Lambertian(Colour colour) {
        this.texture = new SolidColour(colour);
    }

    public Lambertian(Texture texture) {
        this.texture = texture;
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        Vec3 scatterDirection = hitData.normal.add(Vec3.randomUnitVector());
        if (scatterDirection.nearZero()) scatterDirection = hitData.normal;
        Ray scattered = new Ray(hitData.point, scatterDirection, in.time());
        Colour albedo = this.texture.value(hitData.texCoord, hitData.point);
        return new ScatterData(scattered, albedo);
    }
}
