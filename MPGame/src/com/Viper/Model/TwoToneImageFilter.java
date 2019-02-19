package com.Viper.Model;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

public class TwoToneImageFilter implements BufferedImageOp {
	 Color target;
	    int tolerance;

	    public TwoToneImageFilter(Color target, int tolerance) {
	        this.target = target;
	        this.tolerance = tolerance;
	    }

	    //REQUIRED AS OVERRIDE
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

	    public BufferedImage createCompatibleDestImage(
	        BufferedImage src,
	        ColorModel destCM) {
	        BufferedImage bi = new BufferedImage(
	            src.getWidth(),
	            src.getHeight(),
	            BufferedImage.TYPE_INT_RGB);
	        return bi;
	    }

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

	    public Rectangle2D getBounds2D(BufferedImage src) {
	        return new Rectangle2D.Double(0, 0, src.getWidth(), src.getHeight());
	    }

	    public Point2D getPoint2D(
	        Point2D srcPt,
	        Point2D dstPt) {
	        // no co-ord translation
	        return srcPt;
	    }

	    public RenderingHints getRenderingHints() {
	        return null;
	    }
}
