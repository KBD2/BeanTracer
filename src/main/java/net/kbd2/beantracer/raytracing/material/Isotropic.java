package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.texture.SolidColour;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Vec3;

public class Isotropic extends Material {
    private final Texture texture;

    public Isotropic(Colour albedo) {
        this.texture = new SolidColour(albedo);
    }

    public Isotropic(Texture texture) {
        this.texture = texture;
    }

    @Override
    public ScatterData scatter(Ray in, HitData hitData) {
        Ray scattered = new Ray(hitData.point, Vec3.randomUnitVector(), in.time());
        Colour attenuation = this.texture.value(hitData.texCoord, hitData.point);
        return new ScatterData(scattered, attenuation);
    }
}
