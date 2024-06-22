package net.kbd2.beantracer.util.triplet;

public class Point3 extends Vec3 {
    public Point3() {
        super();
    }

    public Point3(Vec3 vec) {
        super(vec);
    }

    public Point3(double x, double y, double z) {
        super(x, y, z);
    }

    public Point3 add(Point3 other) {
        return new Point3(super.add(other));
    }

    public Point3 add(Vec3 other) {
        return new Point3(super.add(other));
    }

    public Point3 sub(Point3 other) {
        return new Point3(super.sub(other));
    }

    public Point3 sub(Vec3 other) {
        return new Point3(super.sub(other));
    }

    public Point3 lerp(Point3 b, double t) {
        return new Point3(super.lerp(b, t));
    }
}
