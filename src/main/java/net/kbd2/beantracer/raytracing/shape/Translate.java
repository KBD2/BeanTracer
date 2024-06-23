package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

public class Translate extends Hittable {
    private final Hittable object;
    private final Vec3 offset;
    private final AABB boundingBox;

    public Translate(Hittable object, Vec3 offset) {
        this.object = object;
        this.offset = offset;
        this.boundingBox = object.boundingBox().add(offset);
    }

    @Override
    public @Nullable HitData hit(Ray ray, Interval rayT) {
        Ray rayOffset = new Ray(ray.orig().sub(offset), ray.dir(), ray.time());

        HitData hitData = object.hit(rayOffset, rayT);
        if (hitData == null) return null;
        hitData.point = hitData.point.add(offset);

        return hitData;
    }

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }
}
