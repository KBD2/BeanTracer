package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.util.triplet.Colour;
import org.jetbrains.annotations.Nullable;

public abstract class Material {
    public record ScatterData (Ray ray, Colour attenuation) {}

    public abstract @Nullable ScatterData scatter(Ray in, HitData hitData);
}
