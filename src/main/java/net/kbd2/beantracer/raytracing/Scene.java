package net.kbd2.beantracer.raytracing;

import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.shape.Hittable;
import net.kbd2.beantracer.util.Interval;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static final List<Hittable> objects = new ArrayList<>();

    public void add(Hittable object) {
        objects.add(object);
    }

    public @Nullable HitData hit(Ray ray, Interval rayT) {
        HitData tempData = null;
        double closest = rayT.max;

        for (Hittable object : objects) {
            HitData data = object.hit(ray, new Interval(rayT.min, closest));
            if (data != null) {
                closest = data.t;
                tempData = data;
            }
        }

        return tempData;
    }
}