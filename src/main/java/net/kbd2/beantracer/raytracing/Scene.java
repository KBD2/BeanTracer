package net.kbd2.beantracer.raytracing;

import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.shape.Hittable;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final List<Hittable> objects = new ArrayList<>();
    private AABB boundingBox;

    public Scene() {
        this.boundingBox = new AABB();
    }

    public Scene(Hittable object) {
        this();
        add(object);
    }

    public void add(Hittable object) {
        objects.add(object);
        this.boundingBox = new AABB(this.boundingBox, object.boundingBox());
    }

    public List<Hittable> getObjects() {
        return new ArrayList<>(this.objects);
    }

    public @Nullable HitData hit(Ray ray, Interval rayT) {
        HitData tempData = null;
        double closest = rayT.max();

        for (Hittable object : objects) {
            HitData data = object.hit(ray, rayT.withMax(closest));
            if (data != null) {
                closest = data.t;
                tempData = data;
            }
        }

        return tempData;
    }
}
