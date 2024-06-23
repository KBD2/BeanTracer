package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.texture.SolidColour;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Vec3;
import org.jetbrains.annotations.Nullable;

public class Metal extends Material {
    private final Texture texture;
    private final double fuzz;

    public Metal(Colour albedo, double fuzz) {
        this(new SolidColour(albedo), fuzz);
    }

    public Metal(Texture texture, double fuzz) {
        this.texture = texture;
        this.fuzz = fuzz;
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        Vec3 reflected = in.dir().reflect(hitData.normal);
        reflected = reflected.unit().add(Vec3.randomUnitVector().mul(fuzz));
        if (reflected.dot(hitData.normal) > 0) {
            Ray scattered = new Ray(hitData.point, reflected, in.time());
            return new ScatterData(scattered, this.texture.value(hitData.texCoord, hitData.point));
        }
        else return null;
    }
}
