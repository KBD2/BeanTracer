package net.kbd2.beantracer.util.triplet;

import net.kbd2.beantracer.util.Util;

import java.util.concurrent.ThreadLocalRandom;

public class Vec3 {
    public final double x;
    public final double y;
    public final double z;

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public double component(int n) {
        if (n == 1) return y;
        if (n == 2) return z;
        return x;
    }

    public Vec3 component(int n, double val) {
        if (n == 1) return new Vec3(this.x, val, this.z);
        if (n == 2) return new Vec3(this.x, this.y, val);
        return new Vec3(val, this.y, this.z);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(
            this.x + other.x,
            this.y + other.y,
            this.z + other.z
        );
    }

    public Vec3 sub(Vec3 other) {
        return new Vec3(
            this.x - other.x,
            this.y - other.y,
            this.z - other.z
        );
    }

    public Vec3 mul(double val) {
        return new Vec3(
            this.x * val,
            this.y * val,
            this.z * val
        );
    }

    public Vec3 mul(Vec3 other) {
        return new Vec3(
            this.x * other.x,
            this.y * other.y,
            this.z * other.z
        );
    }

    public Vec3 div(double val) {
        return new Vec3(
            this.x / val,
            this.y / val,
            this.z / val
        );
    }

    public Vec3 div(Vec3 other) {
        return new Vec3(
            this.x / other.x,
            this.y / other.y,
            this.z / other.z
        );
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double dot(Vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public Vec3 unit() {
        return this.div(this.length());
    }

    public boolean nearZero() {
        double epsilon = 1e-8;
        return Math.abs(this.x) < epsilon && Math.abs(this.y) < epsilon && Math.abs(this.z) < epsilon;
    }

    public Vec3 reflect(Vec3 normal) {
        return sub(normal.mul(2 * dot(normal)));
    }

    public Vec3 refract(Vec3 normal, double refractiveIndexRatio) {
        double cosTheta = Math.min(mul(-1).dot(normal), 1.0);
        Vec3 outPerpendicular = add(normal.mul(cosTheta)).mul(refractiveIndexRatio);
        Vec3 outParallel = normal.mul(-Math.sqrt(Math.abs(1.0 - outPerpendicular.lengthSquared())));
        return outPerpendicular.add(outParallel);
    }

    public Vec3 lerp(Vec3 b, double t) {
        return mul(1.0 - t).add(b.mul(t));
    }

    public static Vec3 random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Vec3(random.nextDouble(), random.nextDouble(),random.nextDouble());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(Util.rand(min, max), Util.rand(min, max), Util.rand(min, max));
    }

    public static Vec3 randomInUnitSphere() {
        while (true) {
            Vec3 p = random(-1, 1);
            if (p.lengthSquared() < 1) return p;
        }
    }

    public static Vec3 randomUnitVector() {
        return randomInUnitSphere().unit();
    }

    public static Vec3 randomOnHemisphere(Vec3 normal) {
        Vec3 onUnitSphere = randomUnitVector();
        if (onUnitSphere.dot(normal) > 0) return onUnitSphere;
        else return onUnitSphere.mul(-1);
    }

    public static Vec3 randomInUnitDisk() {
        while (true) {
            Vec3 p = new Vec3(Util.rand(-1, 1), Util.rand(-1, 1), 0);
            if (p.lengthSquared() < 1) return p;
        }
    }

    public String toString() {
        return "{" + String.format("%.2f", this.x) + ", " + String.format("%.2f", this.y) + ", " + String.format("%.2f", this.z) + "}";
    }
}
