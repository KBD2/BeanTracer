package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.Util;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class RotateY extends Hittable {
    private final Hittable object;
    private final double sinTheta;
    private final double cosTheta;
    private AABB boundingBox;

    public RotateY(Hittable object, double angle) {
        this.object = object;
        double radians = Util.degToRad(angle);

        this.sinTheta = Math.sin(radians);
        this.cosTheta = Math.cos(radians);

        this.boundingBox = object.boundingBox();

        Point3 min = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Point3 max = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    double x = i * this.boundingBox.x().max() + (1 - i) * this.boundingBox.x().min();
                    double y = j * this.boundingBox.y().max() + (1 - j) * this.boundingBox.y().min();
                    double z = k * this.boundingBox.z().max() + (1 - k) * this.boundingBox.z().min();

                    double newX = this.cosTheta * x + this.sinTheta * z;
                    double newZ = -this.sinTheta * x + this.cosTheta * z;

                    Vec3 tester = new Vec3(newX, y, newZ);

                    for (int c = 0; c < 3; c++) {
                        min = new Point3(min.component(c, Math.min(min.component(c), tester.component(c))));
                        max = new Point3(max.component(c, Math.max(max.component(c), tester.component(c))));
                    }
                }
            }
        }

        this.boundingBox = new AABB(min, max);
    }


    @Override
    public @Nullable HitData hit(Ray ray, Interval rayT) {
        Ray rotatedRay = getRotatedRay(ray);

        HitData hitData = this.object.hit(rotatedRay, rayT);

        if (hitData == null) return null;

        Point3 point = hitData.point;
        double newPointX = cosTheta * point.component(0) + sinTheta * point.component(2);
        double newPointZ = -sinTheta * point.component(0) + cosTheta * point.component(2);
        point = new Point3(newPointX, point.y, newPointZ);

        Vec3 normal = hitData.normal;
        double newNormalX = cosTheta * normal.component(0) + sinTheta * normal.component(2);
        double newNormalZ = -sinTheta * normal.component(0) + cosTheta * normal.component(2);
        normal = new Vec3(newNormalX, normal.y, newNormalZ);

        hitData.point = point;
        hitData.normal = normal;

        return hitData;
    }

    private @NotNull Ray getRotatedRay(Ray ray) {
        Point3 origin = ray.orig();
        Vec3 direction = ray.dir();

        double newOriginX = cosTheta * origin.component(0) - sinTheta * origin.component(2);
        double newOriginZ = sinTheta * origin.component(0) + cosTheta * origin.component(2);
        origin = new Point3(newOriginX, origin.y, newOriginZ);

        double newDirectionX = cosTheta * direction.component(0) - sinTheta * direction.component(2);
        double newDirectionZ = sinTheta * direction.component(0) + cosTheta * direction.component(2);
        direction = new Vec3(newDirectionX, direction.y, newDirectionZ);

        return new Ray(origin, direction, ray.time());
    }

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }
}
