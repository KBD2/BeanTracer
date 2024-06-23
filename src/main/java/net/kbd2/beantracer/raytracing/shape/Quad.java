package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.raytracing.texture.TextureCoord;
import net.kbd2.beantracer.util.AABB;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.triplet.Point3;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

public class Quad extends Hittable {
    private final Point3 q;
    private final Vec3 u, v;
    private final Vec3 w;
    private final Material mat;
    private AABB boundingBox;
    private final Vec3 normal;
    private final double d;

    public Quad(Point3 q, Vec3 u, Vec3 v, Material mat) {
        this.q = q;
        this.u = u;
        this.v = v;
        this.mat = mat;

        Vec3 n = u.cross(v);
        this.normal = n.unit();
        this.d = normal.dot(this.q);

        this.w = n.div(n.dot(n));

        setBoundingBox();
    }

    public void setBoundingBox() {
        AABB boundingBoxDiagonal1 = new AABB(this.q, this.q.add(this.u).add(this.v));
        AABB boundingBoxDiagonal2 = new AABB(this.q.add(this.u), this.q.add(this.v));
        this.boundingBox = new AABB(boundingBoxDiagonal1, boundingBoxDiagonal2);
    }

    @Override
    public @Nullable HitData hit(Ray ray, Interval rayT) {
        double denom = this.normal.dot(ray.dir());

        if (Math.abs(denom) < 1e-8) return null;

        double t = (d - normal.dot(ray.orig())) / denom;
        if (!rayT.contains(t)) return null;

        Point3 intersection = ray.at(t);

        Vec3 planarHitpointVec = intersection.sub(this.q);
        double alpha = this.w.dot(planarHitpointVec.cross(this.v));
        double beta = this.w.dot(this.u.cross(planarHitpointVec));

        if (!isInterior(alpha, beta)) return null;

        HitData hitData = new HitData();
        hitData.t = t;
        hitData.point = intersection;
        hitData.mat = this.mat;
        hitData.setFaceNormal(ray, this.normal);
        hitData.texCoord = new TextureCoord(alpha, beta);

        return hitData;
    }

    public boolean isInterior(double a, double b) {
        Interval unitInterval = new Interval(0, 1);
        return unitInterval.contains(a) && unitInterval.contains(b);
    }

    @Override
    public AABB boundingBox() {
        return this.boundingBox;
    }
}
