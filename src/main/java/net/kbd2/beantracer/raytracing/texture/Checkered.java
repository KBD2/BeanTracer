package net.kbd2.beantracer.raytracing.texture;

import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;

public class Checkered extends Texture {
    private final double inverseScale;
    private final Texture even;
    private final Texture odd;

    public Checkered(double scale, Texture even, Texture odd) {
        this.inverseScale = 1.0 / scale;
        this.even = even;
        this.odd = odd;
    }

    public Checkered(double scale, Colour even, Colour odd) {
        this.inverseScale = 1.0 / scale;
        this.even = new SolidColour(even);
        this.odd = new SolidColour(odd);
    }

    @Override
    public Colour value(TextureCoord coord, Point3 point) {
        int x = (int) Math.floor(this.inverseScale * point.x);
        int y = (int) Math.floor(this.inverseScale * point.y);
        int z = (int) Math.floor(this.inverseScale * point.z);

        boolean isEven = (x + y + z) % 2 == 0;
        return isEven ? even.value(coord, point) : odd.value(coord, point);
    }
}
