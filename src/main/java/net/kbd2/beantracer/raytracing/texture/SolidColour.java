package net.kbd2.beantracer.raytracing.texture;

import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;

public class SolidColour extends Texture {
    private final Colour albedo;

    public SolidColour(Colour albedo) {
        this.albedo = albedo;
    }

    public SolidColour(double red, double green, double blue) {
        this.albedo = new Colour(red, green, blue);
    }

    @Override
    public Colour value(TextureCoord coord, Point3 point) {
        return this.albedo;
    }
}
