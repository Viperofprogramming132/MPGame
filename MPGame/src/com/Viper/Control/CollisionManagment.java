package com.Viper.Control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

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
	
	private Rectangle2D _VehicleCollisionBox;
	
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
        
        if(_VehicleSW == null || _VehicleSW.isDone())
        {
	    	_VehicleSW = new SwingWorker<Object, Object>()
			{
	
				@Override
				protected String doInBackground() throws Exception {
					_VehicleArea = getOutline(Color.WHITE, _VehicleMask);
					if (angle == 0)
						_VehicleCollisionBox = _VehicleArea.getBounds2D();
					return "";
				}
		
			};
			_VehicleSW.execute();
        }
	}
	
	//AABB collision
	@SuppressWarnings("static-access")
	public boolean CheckVehicleCollision(Point RequestingVehicleLocation, double RequestingVehicleRotation, Point[] VehicleLocations, Double[] VehicleRotations)
	{
		boolean result = false;
		
		if(_VehicleCollisionBox != null)
		{
			AffineTransform tx = new AffineTransform();
			tx.rotate(RequestingVehicleRotation, _VehicleCollisionBox.getCenterX(), _VehicleCollisionBox.getCenterY());
			
			Shape RequestingVehicleCollisionBox = tx.createTransformedShape((Rectangle2D)_VehicleCollisionBox.clone()); 
			for (int i = 0; i < VehicleLocations.length; i++)
			{			
				tx = new AffineTransform();
				tx.rotate(VehicleRotations[i], _VehicleCollisionBox.getCenterX(), _VehicleCollisionBox.getCenterY());
				
				Shape shape = tx.createTransformedShape((Rectangle2D)_VehicleCollisionBox.clone());
				
				
				if(RequestingVehicleCollisionBox.intersects((VehicleLocations[i].x + 20) - RequestingVehicleLocation.x, (VehicleLocations[i].y + 20) - RequestingVehicleLocation.y, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight()))
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
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
    
    @SuppressWarnings("static-access")
	public boolean CheckForCollision(int x, int y)
    {
    	if (_MapArea == null || _VehicleArea == null)
    		return false;
    	boolean result = false;
    	
    	
    	//Create a iterator so it can check that every point is not colliding
    	PathIterator PI = _MapArea.getPathIterator(null);
    	
    	//Checks if the Vehicle collides with the collision map created of the map
    	//+1 on both due to shrinking of the map for speed causes so offset issues this is to fix them
    	//ONLY CHECKS THE MAP
    	result = _VehicleArea.intersects(PI, (x / 16) + 1, (y / 16) + 1, 1, 1);
    	
    	return result; 
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
        g2d.dispose();

        return rotated;
    }
    
    public Rectangle2D getCollisionBox()
    {
    	return _VehicleCollisionBox;
    }
    
    //Used for the checkpoint system as they do not need to be accurate
    public boolean intersects(JLabel testa, JLabel testb){
        Area areaA = new Area(testa.getBounds());
        Area areaB = new Area(testb.getBounds());

        return areaA.intersects(areaB.getBounds2D());
    }

	public void Close() {
		_MapSW.cancel(true);
		_VehicleSW.cancel(true);
		
	}
}

