package net.kbd2.beantracer.util;

public class Interval {
    public double min;
    public double max;

    public static final Interval empty = new Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    public static final Interval universe = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public Interval() {
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }

    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public boolean contains(double x) {
        return min <= x && x <= max;
    }

    public boolean surrounds(double x) {
        return min < x && x < max;
    }

    public double clamp(double x) {
        return Math.max(min, Math.min(x, max));
    }
}
