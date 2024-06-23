package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HittableList extends Hittable {
    private final List<Hittable> objects = new ArrayList<>();
    private AABB boundingBox;

    public HittableList() {
        this.boundingBox = new AABB();
    }

    public HittableList(Hittable object) {
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

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }
}
