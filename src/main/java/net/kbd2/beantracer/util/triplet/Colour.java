package net.kbd2.beantracer.util.triplet;

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

    public Colour add(Colour other) {
        return new Colour(super.add(other));
    }

    public Colour sub(Colour other) {
        return new Colour(super.sub(other));
    }

    public Colour mul(Colour other) {
        return new Colour(super.mul(other));
    }

    public Colour div(Colour other) {
        return new Colour(super.div(other));
    }

    public Colour lerp(Colour b, double t) {
        return new Colour(super.lerp(b, t));
    }
}
