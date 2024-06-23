package net.kbd2.beantracer.raytracing.material;

import net.kbd2.beantracer.raytracing.Ray;
import net.kbd2.beantracer.raytracing.shape.HitData;
import net.kbd2.beantracer.raytracing.texture.SolidColour;
import net.kbd2.beantracer.raytracing.texture.Texture;
import net.kbd2.beantracer.raytracing.texture.TextureCoord;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;
import org.jetbrains.annotations.Nullable;

public class DiffuseLight extends Material {
    private final Texture texture;

    public DiffuseLight(Texture texture) {
        this.texture = texture;
    }

    public DiffuseLight(Colour emit) {
        this.texture = new SolidColour(emit);
    }

    @Override
    public @Nullable ScatterData scatter(Ray in, HitData hitData) {
        return null;
    }

    @Override
    public Colour emitted(TextureCoord coord, Point3 point) {
        return this.texture.value(coord, point);
    }
}
