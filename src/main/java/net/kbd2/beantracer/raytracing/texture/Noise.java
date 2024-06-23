package net.kbd2.beantracer.raytracing.texture;

import net.kbd2.beantracer.util.Perlin;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;

public class Noise extends Texture {
    public enum NoiseType {
        PERLIN,
        TURBULENCE,
        MARBLED
    }

    private final Perlin noise;
    private final double scale;
    private final NoiseType type;

    public Noise(double scale) {
        this(NoiseType.PERLIN, scale);
    }

    public Noise(NoiseType type, double scale) {
        this.noise = new Perlin();
        this.scale = scale;
        this.type = type;
    }

    @Override
    public Colour value(TextureCoord coord, Point3 point) {
        return switch (this.type) {
            case PERLIN -> new Colour(1, 1, 1).mul(0.5 * (1.0 + noise.noise(new Point3(point.mul(scale)))));
            case TURBULENCE -> new Colour(1, 1, 1).mul(noise.turbulence(point, 7));
            case MARBLED -> new Colour(0.5, 0.5, 0.5).mul(1 + Math.sin(scale * point.z + 10 * noise.turbulence(point, 7)));
        };
    }
}
