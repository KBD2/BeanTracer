package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.Point3;
import net.kbd2.beantracer.util.Vec3;

public class Sphere extends Hittable {
    private final Point3 centre;
    private final double radius;
    private final Material mat;

    public Sphere(Point3 centre, double radius, Material mat) {
        this.centre = centre;
        this.radius = radius;
        this.mat = mat;
    }

    @Override
    public HitData hit(Ray ray, Interval rayT) {
        Vec3 oc = this.centre.sub(ray.orig());
        double a = ray.dir().lengthSquared();
        double h = ray.dir().dot(oc);
        double c = oc.lengthSquared() - this.radius * this.radius;
        double discriminant = h * h - a * c;

        if (discriminant < 0) return null;

        double sqrtDist = Math.sqrt(discriminant);

        double root = (h - sqrtDist) / a;
        if (!rayT.surrounds(root)) {
            root = (h + sqrtDist) / a;
            if (!rayT.surrounds(root)) return null;
        }

        HitData hitData = new HitData();

        hitData.t = root;
        hitData.point = ray.at(root);
        Vec3 outwardNormal = hitData.point.sub(this.centre).div(this.radius);
        hitData.setFaceNormal(ray, outwardNormal);
        hitData.mat = this.mat;

        return hitData;
    }
}
