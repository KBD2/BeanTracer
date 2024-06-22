package net.kbd2.beantracer.util;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.Scene;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.shape.Hittable;

import java.util.Comparator;
import java.util.List;

public class BVHNode extends Hittable {
    private AABB boundingBox;
    private final Hittable left;
    private final Hittable right;

    public BVHNode(Scene scene) {
        this(scene.getObjects());
    }

    public BVHNode(List<Hittable> objects) {
        this.boundingBox = AABB.empty;
        for (Hittable object : objects) {
            this.boundingBox = new AABB(this.boundingBox, object.boundingBox());
        }

        int axis = this.boundingBox.longestAxis();
        Comparator<Hittable> comparator = Comparator.comparingDouble(x -> x.boundingBox().axisInterval(axis).min());

        int numObjects = objects.size();
        if (numObjects == 1) {
            this.left = this.right = objects.getFirst();
        } else if (numObjects == 2) {
            this.left = objects.getFirst();
            this.right = objects.getLast();
        } else {
            objects.sort(comparator);
            int mid = numObjects / 2;
            this.left = new BVHNode(objects.subList(0, mid));
            this.right = new BVHNode(objects.subList(mid, numObjects));
        }

    }

    @Override
    public HitData hit(Ray ray, Interval rayT) {
        if (!this.boundingBox.hit(ray, rayT)) return null;
        HitData hitLeft = this.left.hit(ray, rayT);
        HitData hitRight = this.right.hit(ray, hitLeft == null ? rayT : rayT.withMax(hitLeft.t));

        if (hitLeft == null && hitRight == null) return null;
        if (hitLeft == null) return hitRight;
        if (hitRight == null) return hitLeft;

        return hitLeft.t < hitRight.t ? hitLeft : hitRight;
    }

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }
}
