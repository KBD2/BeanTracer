package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.util.Interval;
import org.jetbrains.annotations.Nullable;

public abstract class Hittable {
    public abstract @Nullable HitData hit(Ray ray, Interval rayT);
}
