package com.Viper.Model;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * Used by the Collision Management system to create Black and white images
 * 
 * Thanks to https://stackoverflow.com/questions/7218309/smoothing-a-jagged-path for supplying this
 * @author Aidan
 *
 */
public class TwoToneImageFilter implements BufferedImageOp {
	/**
	 * The colour that the system creates the black and white around
	 * This colour +/- the tolerance will be set to white
	 */
	Color target;
	
	/**
	 * The tolerance that the colour can change before it is set to black
	 */
    int tolerance;

    /**
     * Creates a Black and white image that is created off the given colour and tolerance
     * @param target The target colour to create the black and white image around. This colour will be set to white
     * @param tolerance The tolerance to +/- from the colour to get the black and white sections
     */
    public TwoToneImageFilter(Color target, int tolerance) {
        this.target = target;
        this.tolerance = tolerance;
    }

    /**
     * Checks if the given pixel colour is included in the image within the tolerance 
     * @param pixel The colour to check for
     * @return True if the colour is found otherwise false
     */
    private boolean isIncluded(Color pixel) {
        int rT = target.getRed();
        int gT = target.getGreen();
        int bT = target.getBlue();
        int rP = pixel.getRed();
        int gP = pixel.getGreen();
        int bP = pixel.getBlue();
        return(
            (rP-tolerance<=rT) && (rT<=rP+tolerance) &&
            (gP-tolerance<=gT) && (gT<=gP+tolerance) &&
            (bP-tolerance<=bT) && (bT<=bP+tolerance) );
    }

    /**
     * Creates a BufferedImage that is of the same size as the current image
     */
    public BufferedImage createCompatibleDestImage(
        BufferedImage src,
        ColorModel destCM) {
        BufferedImage bi = new BufferedImage(
            src.getWidth(),
            src.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        return bi;
    }

    /**
     * Gets each pixel at each location and sets it to white if it contains the colour within tolerance otherwise sets it to black
     */
    public BufferedImage filter(
        BufferedImage src,
        BufferedImage dest) {

        if (dest==null) {
            dest = createCompatibleDestImage(src, null);
        }

        for (int x=0; x<src.getWidth(); x++) {
            for (int y=0; y<src.getHeight(); y++) {
                Color pixel = new Color(src.getRGB(x,y));
                Color write = Color.BLACK;
                if (isIncluded(pixel)) {
                    write = Color.WHITE;
                }
                dest.setRGB(x,y,write.getRGB());
            }
        }

        return dest;
    }

    /**
     * Gets the Rectangle2D that would surround all the contents of the image
     */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle2D.Double(0, 0, src.getWidth(), src.getHeight());
    }

    /**
     * Required override
     */
    public Point2D getPoint2D(
        Point2D srcPt,
        Point2D dstPt) {
        // no co-ord translation
        return srcPt;
    }

    /**
     * As there are no rendering hints return null
     */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
