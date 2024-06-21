package net.kbd2.beantracer.raytracing.shape;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.material.Material;
import net.kbd2.beantracer.util.Point3;
import net.kbd2.beantracer.util.Vec3;

public class HitData {
    public Point3 point;
    public Vec3 normal;
    public double t;
    public boolean frontFace;
    public Material mat;

    public void setFaceNormal(Ray ray, Vec3 outwardNormal) {
        this.frontFace = ray.dir().dot(outwardNormal) < 0;
        this.normal = frontFace ? outwardNormal : outwardNormal.mul(-1);
    }
}
