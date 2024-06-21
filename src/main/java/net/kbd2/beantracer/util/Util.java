package net.kbd2.beantracer.util;

import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static double degToRad(double angle) {
        return angle * Math.PI / 180;
    }

    public static double rand(double min, double max) {
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }
}
