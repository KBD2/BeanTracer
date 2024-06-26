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

    public static HittableList box(Point3 a, Point3 b, Material mat) {
        HittableList sides = new HittableList();

        Point3 min = new Point3(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
        Point3 max = new Point3(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));

        Vec3 dX = new Vec3(max.x - min.x, 0, 0);
        Vec3 dY = new Vec3(0, max.y - min.y, 0);
        Vec3 dZ = new Vec3(0, 0, max.z - min.z);

        sides.add(new Quad(new Point3(min.x, min.y, max.z),  dX,  dY, mat)); // front
        sides.add(new Quad(new Point3(max.x, min.y, max.z), dZ.mul(-1),  dY, mat)); // right
        sides.add(new Quad(new Point3(max.x, min.y, min.z), dX.mul(-1),  dY, mat)); // back
        sides.add(new Quad(new Point3(min.x, min.y, min.z),  dZ,  dY, mat)); // left
        sides.add(new Quad(new Point3(min.x, max.y, max.z),  dX, dZ.mul(-1), mat)); // top
        sides.add(new Quad(new Point3(min.x, min.y, min.z),  dX,  dZ, mat)); // bottom

        return sides;
    }
}
