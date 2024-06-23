package net.kbd2.beantracer.util;

public class Interval {
    private final double min;
    private final double max;

    public static final Interval empty = new Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    public static final Interval universe = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public Interval() {
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
    }

    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public Interval(Interval a, Interval b) {
        this.min = Math.min(a.min, b.min);
        this.max = Math.max(a.max, b.max);
    }

    public double min() {
        return this.min;
    }

    public Interval withMin(double min) {
        return new Interval(min, this.max);
    }

    public double max() {
        return this.max;
    }

    public Interval withMax(double max) {
        return new Interval(this.min, max);
    }

    public Interval add(double displacement) {
        return new Interval(this.min + displacement, this.max + displacement);
    }

    public boolean contains(double x) {
        return this.min <= x && x <= this.max;
    }

    public boolean surrounds(double x) {
        return this.min < x && x < this.max;
    }

    public double clamp(double x) {
        return Math.max(this.min, Math.min(x, this.max));
    }

    public Interval expand(double delta) {
        double padding = delta / 2;
        return new Interval(this.min - padding, this.max + padding);
    }

    public double size() {
        return this.max - this.min;
    }

    public String toString() {
        return "(" + String.format("%.2f", this.min) + " to " + String.format("%.2f", this.max) + ")";
    }
}
