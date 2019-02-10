package com.Viper.Control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.Viper.Debug.ShowImageForm;
import com.Viper.Model.TwoToneImageFilter;

//Using https://stackoverflow.com/questions/7218309/smoothing-a-jagged-path
public class CollisionManagment {

	
	private TwoToneImageFilter _CollisionMapFilter;
	private BufferedImage _MapMask;
	private BufferedImage _Map;
	private GeneralPath _MapArea;
	
	private TwoToneImageFilter _CollisionVehicleFilter;
	private BufferedImage _VehicleMask;
	private GeneralPath _VehicleArea;
	
	private SwingWorker<?, ?> _MapSW;
	private SwingWorker<?, ?> _VehicleSW;
	
	ShowImageForm s;
	
	//Shrinks everything down in size to allow for faster processing
	public CollisionManagment(BufferedImage map)
	{
		map = scale(map, 0.5);
		_CollisionMapFilter = new TwoToneImageFilter(Color.BLACK, 20);
		_Map = map;
		
		_MapMask = new BufferedImage(
				_Map.getWidth(),
				_Map.getHeight(),
                BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g = _MapMask.createGraphics();
        g.drawImage(_Map, _CollisionMapFilter, 0, 0);
        
        g.dispose();
        
        _MapSW = new SwingWorker<Object, Object>()
        		{

					@Override
					protected String doInBackground() throws Exception {
						_MapArea = getOutline(Color.BLACK, _MapMask);
						new ShowImageForm(_MapArea);
						return "";
					}
        	
        		};
        _MapSW.execute();
	}
	
	public void CreateVehicleMask(BufferedImage Vehicle, double angle)
	{
		scale(Vehicle, 0.5);
		_CollisionVehicleFilter = new TwoToneImageFilter(Color.WHITE, 204);
		
		Vehicle = rotateImageByDegrees(Vehicle, angle);
		
		_VehicleMask = new BufferedImage(
				Vehicle.getWidth(),
				Vehicle.getHeight(),
                BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g = _VehicleMask.createGraphics();
        g.drawImage(Vehicle, _CollisionVehicleFilter, 0, 0);
        
        g.dispose();
        if (s == null)
        	s = new ShowImageForm(new ImageIcon(_VehicleMask));
        else
        	s.updateLabel(new ImageIcon(_VehicleMask));
        
        if(_VehicleSW == null || _VehicleSW.isDone())
        {
	    	_VehicleSW = new SwingWorker<Object, Object>()
			{
	
				@Override
				protected String doInBackground() throws Exception {
					_VehicleArea = getOutline(Color.BLACK, _VehicleMask);
					return "";
				}
		
			};
			_VehicleSW.execute();
        }
		
		
	}
	
    public GeneralPath getOutline(Color target, BufferedImage bi) {
        // construct the GeneralPath
        GeneralPath gp = new GeneralPath();

        boolean cont = false;
        int targetRGB = target.getRGB();
        for (int xx=0; xx<bi.getWidth(); xx++) {
            for (int yy=0; yy<bi.getHeight(); yy++) {
                if (bi.getRGB(xx,yy)==targetRGB) {
                    if (cont) {
                        gp.lineTo(xx,yy);
                        gp.lineTo(xx,yy+1);
                        gp.lineTo(xx+1,yy+1);
                        gp.lineTo(xx+1,yy);
                        gp.lineTo(xx,yy);
                    } else {
                        gp.moveTo(xx,yy);
                    }
                    cont = true;
                } else {
                    cont = false;
                }
            }
            cont = false;
        }
        gp.closePath();

        // construct the Area from the GP & return it
        return gp;
    }
    
    public boolean CheckForCollision(int x, int y)
    {
    	if (_MapArea == null || _VehicleArea == null)
    		return false;
    	boolean result = false;
    	
    	//So it doesnt overwrite the map
    	
    	PathIterator PI = _MapArea.getPathIterator(null);
    	
    	result = _VehicleArea.intersects(PI, x / 16, y / 16, 1, 1);
    	
    	return result; 
    }
    
    public BufferedImage get_MapMask()
    {
    	return _MapMask;
    }
    
    public BufferedImage scale(BufferedImage sbi, double scale) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage((int)(sbi.getWidth() * scale), (int)(sbi.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
    
    public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = angle;
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage rotated = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((img.getWidth() - w) / 2, (img.getHeight() - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        g2d.dispose();

        return rotated;
    }
}

