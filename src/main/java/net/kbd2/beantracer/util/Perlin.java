package net.kbd2.beantracer.util;

import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class Perlin {
    private static final int pointCount = 256;

    private final Vec3[] randVec;
    private final int[] permX;
    private final int[] permY;
    private final int[] permZ;

    public Perlin() {
        randVec = new Vec3[pointCount];
        for (int i = 0; i < pointCount; i++) randVec[i] = Vec3.random(-1, 1).unit();

        permX = perlinGeneratePerm();
        permY = perlinGeneratePerm();
        permZ = perlinGeneratePerm();
    }

    public double noise(Point3 point) {
        double u = point.x - Math.floor(point.x);
        double v = point.y - Math.floor(point.y);
        double w = point.z - Math.floor(point.z);

        int i = (int) Math.floor(point.x);
        int j = (int) Math.floor(point.y);
        int k = (int) Math.floor(point.z);

        Vec3[][][] c = new Vec3[2][2][2];

        for (int dI = 0; dI < 2; dI++) {
            for (int dJ = 0; dJ < 2; dJ++) {
                for (int dK = 0; dK < 2; dK++) {
                    c[dI][dJ][dK] = randVec[
                            permX[(i + dI) & 255] ^
                                    permY[(j + dJ) & 255] ^
                                    permZ[(k + dK) & 255]
                            ];
                }
            }
        }

        return perlinInterp(c, u, v, w);
    }

    public double turbulence(Point3 point, int depth) {
        double accum = 0;
        Point3 tempPoint = point;
        double weight = 1;

        for (int i = 0; i < depth; i++) {
            accum += weight * noise(tempPoint);
            weight *= 0.5;
            tempPoint = new Point3(tempPoint.mul(2));
        }

        return Math.abs(accum);
    }

    private static int[] perlinGeneratePerm() {
        int[] points = new int[pointCount];
        for (int i = 0; i < pointCount; i++) points[i] = i;

        permute(points);
        return points;
    }

    private static void permute(int[] p) {
        for (int i = pointCount - 1; i > 0; i--) {
            int target = ThreadLocalRandom.current().nextInt(0, i);
            int tmp = p[i];
            p[i] = p[target];
            p[target] = tmp;
        }
    }

    private static double perlinInterp(Vec3[][][] c, double u, double v, double w) {
        double uu = u * u * (3 - 2 * u);
        double vv = v * v * (3 - 2 * v);
        double ww = w * w * (3 - 2 * w);
        double accum = 0;

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++) {
                    Vec3 weightV = new Vec3 (u - i, v - j, w - k);
                    accum += (i * uu + (1 - i) * (1 - uu))
                            * (j * vv + (1 - j) * (1 - vv))
                            * (k * ww + (1 - k) * (1 - ww))
                            * c[i][j][k].dot(weightV);
                }

        return accum;
    }
}
