package net.kbd2.beantracer.util;

public class Colour extends Vec3 {
    public Colour() {
        super();
    }

    public Colour(Vec3 vec) {
        super(vec);
    }

    public Colour(double r, double g, double b) {
        super(r, g, b);
    }

    public double r() {
        return this.x;
    }
    public double g() {
        return this.y;
    }
    public double b() {
        return this.z;
    }
}
