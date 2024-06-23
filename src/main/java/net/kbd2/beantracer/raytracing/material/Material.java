package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.texture.TextureCoord;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import org.jetbrains.annotations.Nullable;

public abstract class Material {
    public record ScatterData (Ray ray, Colour attenuation) {}

    public Colour emitted(TextureCoord coord, Point3 p) {
        return new Colour(0, 0, 0);
    }

    public abstract @Nullable ScatterData scatter(Ray in, HitData hitData);
}
