package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.texture.TextureCoord;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;

public class Sphere extends Hittable {
    private final Point3 centre;
    private final double radius;
    private final Material mat;
    private final Vec3 velocity;
    private final AABB boundingBox;

    public Sphere(Point3 centre, double radius, Material mat) {
        this(centre, null, radius, mat);
    }

    public Sphere(Point3 centreStart, Point3 centreEnd, double radius, Material mat) {
        this.centre = centreStart;
        this.radius = radius;
        this.mat = mat;
        Vec3 rVec = new Vec3(radius, radius, radius);
        if (centreEnd != null) {
            this.velocity = centreEnd.sub(centreStart);
            AABB box1 = new AABB(centreStart.sub(rVec), centreStart.add(rVec));
            AABB box2 = new AABB(centreEnd.sub(rVec), centreEnd.add(rVec));
            this.boundingBox = new AABB(box1, box2);
        } else {
            this.velocity = null;
            this.boundingBox = new AABB(centre.sub(rVec), centre.add(rVec));
        }
    }

    @Override
    public HitData hit(Ray ray, Interval rayT) {
        Point3 currentCentre = this.velocity != null ? sphereCentre(ray.time()) : this.centre;

        Vec3 oc = currentCentre.sub(ray.orig());
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
        Vec3 outwardNormal = hitData.point.sub(currentCentre).unit();
        hitData.setFaceNormal(ray, outwardNormal);
        hitData.mat = this.mat;
        hitData.texCoord = getSphereUV(new Point3(outwardNormal));

        return hitData;
    }

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }

    private Point3 sphereCentre(double time) {
        return this.centre.add(this.velocity.mul(time));
    }

    private static TextureCoord getSphereUV(Point3 point) {
        double theta = Math.acos(-point.y);
        double phi = Math.atan2(-point.z, point.x) + Math.PI;

        double u = phi / (2.0 * Math.PI);
        double v = theta / Math.PI;
        return new TextureCoord(u, v);
    }
}
