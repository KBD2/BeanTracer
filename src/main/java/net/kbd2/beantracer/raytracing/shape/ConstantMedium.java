package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.material.Isotropic;
import net.kbd2.beantracer.raytracing.texture.SolidColour;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class ConstantMedium extends Hittable {
    private final Hittable boundary;
    private final double negativeInverseDensity;
    private final Material phaseFunction;

    public ConstantMedium(Hittable boundary, double density, Colour albedo) {
        this(boundary, density, new SolidColour(albedo));
    }

    public ConstantMedium(Hittable boundary, double density, Texture texture) {
        this.boundary = boundary;
        this.negativeInverseDensity = -1.0 / density;
        this.phaseFunction = new Isotropic(texture);
    }

    @Override
    public @Nullable HitData hit(Ray ray, Interval rayT) {
        HitData data1, data2;

        data1 = boundary.hit(ray, Interval.universe);
        if (data1 == null) return null;

        data2 = boundary.hit(ray, new Interval(data1.t + 0.001, Double.POSITIVE_INFINITY));
        if (data2 == null) return null;

        if (data1.t < rayT.min()) data1.t = rayT.min();
        if (data2.t > rayT.max()) data2.t = rayT.max();

        if (data1.t < 0) data1.t = 0;

        double rayLength = ray.dir().length();
        double distanceInsideBoundary = (data2.t - data1.t) * rayLength;
        double hitDistance = this.negativeInverseDensity * Math.log(ThreadLocalRandom.current().nextDouble());

        if (hitDistance > distanceInsideBoundary) return null;

        HitData hitData = new HitData();
        hitData.t = data1.t + hitDistance / rayLength;
        hitData.point = ray.at(hitData.t);
        hitData.normal = new Vec3(0, 1, 0);
        hitData.frontFace = true;
        hitData.mat = this.phaseFunction;

        return hitData;
    }

    @Override
    public AABB boundingBox() {
        return this.boundary.boundingBox();
    }
}
