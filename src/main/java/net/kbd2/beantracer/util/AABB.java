package net.kbd2.beantracer.util;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;

public class AABB {
    public static final AABB empty = new AABB(Interval.empty, Interval.empty, Interval.empty);
    public static final AABB universe = new AABB(Interval.universe, Interval.universe, Interval.universe);

    private Interval x, y, z;

    public AABB() {
        this.x = new Interval();
        this.y = new Interval();
        this.z = new Interval();
    }

    public AABB(Point3 a, Point3 b) {
        this.x = (a.x <= b.x) ? new Interval(a.x, b.x) : new Interval(b.x, a.x);
        this.y = (a.y <= b.y) ? new Interval(a.y, b.y) : new Interval(b.y, a.y);
        this.z = (a.z <= b.z) ? new Interval(a.z, b.z) : new Interval(b.z, a.z);

        padToMinimums();
    }

    public AABB(AABB a, AABB b) {
        this.x = new Interval(a.x, b.x);
        this.y = new Interval(a.y, b.y);
        this.z = new Interval(a.z, b.z);
    }

    public AABB(Interval x, Interval y, Interval z) {
        this.x = x;
        this.y = y;
        this.z = z;

        padToMinimums();
    }

    public Interval x() {
        return this.x;
    }

    public Interval y() {
        return this.y;
    }

    public Interval z() {
        return this.z;
    }

    public AABB add(Vec3 offset) {
        return new AABB(this.x.add(offset.x), this.y.add(offset.y), this.z.add(offset.z));
    }

    public Interval axisInterval(int n) {
        if (n == 1) return y;
        if (n == 2) return z;
        return x;
    }

    public boolean hit(Ray ray, Interval rayT) {
        Point3 rayOrig = ray.orig();
        Vec3 rayDir = ray.dir();

        for (int axis = 0; axis < 3; axis++) {
            Interval ax = axisInterval(axis);
            double axisInverseComponent = 1.0 / rayDir.component(axis);

            double t0 = (ax.min() - rayOrig.component(axis)) * axisInverseComponent;
            double t1 = (ax.max() - rayOrig.component(axis)) * axisInverseComponent;

            if (t0 < t1) {
                if (t0 > rayT.min()) rayT = rayT.withMin(t0);
                if (t1 < rayT.max()) rayT = rayT.withMax(t1);
            } else {
                if (t1 > rayT.min()) rayT = rayT.withMin(t1);
                if (t0 < rayT.max()) rayT = rayT.withMax(t0);
            }

            if (rayT.max() <= rayT.min()) return false;
        }
        return true;
    }

    public int longestAxis() {
        if (this.x.size() > this.y.size()) {
            return this.x.size() > this.z.size() ? 0 : 2;
        } else {
            return this.y.size() > this.z.size() ? 1 : 2;
        }
    }

    private void padToMinimums() {
        double delta = 0.0001;
        if (x.size() < delta) x = x.expand(delta);
        if (y.size() < delta) y = y.expand(delta);
        if (z.size() < delta) z = z.expand(delta);
    }

    public String toString() {
        return "AABB[X: " + this.x + ", Y: " + this.y + ", Z: " + this.z+ "]";
    }
}
