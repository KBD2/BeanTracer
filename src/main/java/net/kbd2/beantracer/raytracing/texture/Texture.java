package net.kbd2.beantracer.raytracing.texture;

import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;

public abstract class Texture {
    public abstract Colour value(TextureCoord coord, Point3 point);
}
