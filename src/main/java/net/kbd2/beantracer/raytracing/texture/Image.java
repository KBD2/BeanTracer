package net.kbd2.beantracer.raytracing.texture;

import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import net.kbd2.beantracer.util.Interval;
import net.kbd2.beantracer.util.triplet.Colour;
import net.kbd2.beantracer.util.triplet.Point3;

import java.io.File;

public class Image extends Texture {
    private final int height, width;
    private final Colour[][] image;

    public Image(String fileName) {
        PngReader reader = new PngReader(new File(fileName));
        this.width = reader.imgInfo.cols;
        this.height = reader.imgInfo.rows;

        double colourScale = 1.0 / 255.0;
        image = new Colour[height][width];
        for (int y = 0; y < this.height; y++) {
            ImageLineInt line = (ImageLineInt) reader.readRow();
            for (int x = 0; x < this.width; x++) {
                int pixel = ImageLineHelper.getPixelRGB8(line, x);
                Colour colour = new Colour(colourScale * ((pixel >> 16) & 0xff),  colourScale * ((pixel >> 8) & 0xff), colourScale * (pixel & 0xff));
                this.image[y][x] = colour;
            }
        }

        reader.end();
    }

    @Override
    public Colour value(TextureCoord coord, Point3 point) {
        if (this.height <= 0 || this.width <= 0) return new Colour(1, 0, 0);
        double u = new Interval(0, 1).clamp(coord.u());
        double v = 1.0 - new Interval(0, 1).clamp(coord.v()); // v is greater downwards in texture coords

        int x = (int) (u * (this.width - 1));
        int y = (int) (v * (this.height - 1));

        return this.image[y][x];
    }
}
