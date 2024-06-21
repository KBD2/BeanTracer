package net.kbd2.beantracer.raytracing;

import net.kbd2.beantracer.util.Point3;
import net.kbd2.beantracer.util.Vec3;

public record Ray(Point3 orig, Vec3 dir) {

    public Point3 at(double t) {
        return new Point3(orig.add(dir.mul(t)));
    }
}
