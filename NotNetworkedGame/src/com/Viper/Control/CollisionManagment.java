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

import com.Viper.Model.TwoToneImageFilter;
import com.Viper.UI.UIControl;

/**
 * Collision Management uses a colour based system to dynamically make the track collision
 * It also deals with the vehicle collision with bounding boxes
 * Finds when the players have crossed the checkpoints
 *   
 * Using https://stackoverflow.com/questions/7218309/smoothing-a-jagged-path
 * 
 * @author Aidan
 *
 */
public class CollisionManagment {
	
	double _ScaleX = 0;
	
	double _ScaleY = 0;

	/**
	 * Used to create a black and white image of the map
	 */
	private TwoToneImageFilter _CollisionMapFilter;
	
	/**
	 * The Mask of the map
	 */
	private BufferedImage _MapMask;
	
	/**
	 * The original Map image
	 */
	private BufferedImage _Map;
	
	/**
	 * The shape of the path the vehicle should stay on
	 */
	private GeneralPath _MapArea;
	
	/**
	 * Used to create a black and white image of the vehicle
	 */
	private TwoToneImageFilter _CollisionVehicleFilter;
	
	/**
	 * The Mask of the vehicle
	 */
	private BufferedImage _VehicleMask;
	
	/**
	 * The shape of the vehicle
	 */
	private GeneralPath _VehicleArea;
	
	/**
	 * The worker that creates the shape of the map when the game starts
	 */
	private SwingWorker<?, ?> _MapSW;
	
	/**
	 * The worker that creates the vehicle shape at the rotation each frame
	 */
	private SwingWorker<?, ?> _VehicleSW;
	
	/**
	 * The bounding box of the vehicle
	 */
	private Rectangle2D _VehicleCollisionBox;
	
	/**
	 * Creates a collision management object
	 * @param map The map that is going to be used so the collision system can be made dynamically
	 */
	public CollisionManagment(BufferedImage map)
	{
		//Shrinks the map down for faster processing for the loss of accuracy 
		map = scale(map, 0.5);
		
		//Create the black and white image with the base of black and a threshold of 20
		_CollisionMapFilter = new TwoToneImageFilter(Color.BLACK, 20);
		_Map = map;
		
		//Create the mask from the filter
		_MapMask = new BufferedImage(
				_Map.getWidth(),
				_Map.getHeight(),
                BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g = _MapMask.createGraphics();
        g.drawImage(_Map, _CollisionMapFilter, 0, 0);
        
        g.dispose();
        
        //Create the image from the mask
        _MapSW = new SwingWorker<Object, Object>()
        		{

					@Override
					protected String doInBackground() throws Exception {
						_MapArea = getOutline(Color.BLACK, _MapMask);
						CalcScale();
						return "";
					}
        	
        		};
        _MapSW.execute();
	}
	
	/**
	 * Calculate the size to scale the collision map to
	 * 
	 * Sets scaleX and scaleY
	 */
	private void CalcScale()
	{
		Rectangle2D mapBounds = _MapArea.getBounds2D();
		
		double mapX = mapBounds.getWidth();
		double mapY = mapBounds.getHeight();
		
		double frameX = UIControl.GetInstance().getWidth();
		//-25 for the menu bar
		double frameY = UIControl.GetInstance().getHeight() - 25;
		
		_ScaleX = frameX / mapX;
		_ScaleY = frameY / mapY;
	}
	
	/**
	 * Creates the vehicle mask out of the given vehicle image and the angle
	 * @param Vehicle The Vehicle image the the mask should be made from
	 * @param angle The angle to rotate the image to to match the vehicle
	 */
	public void CreateVehicleMask(BufferedImage Vehicle, double angle)
	{
		//Scale the image of the vehicle down to match with the map
		scale(Vehicle, 0.5);
		
		//Create the black white filter which get everything but the background (transparacy becomes black)
		_CollisionVehicleFilter = new TwoToneImageFilter(Color.WHITE, 244);
		
		//Rotate the image by the angle
		Vehicle = rotateImageByDegrees(Vehicle, angle);
		
		//Create the mask from the image
		_VehicleMask = new BufferedImage(
				Vehicle.getWidth(),
				Vehicle.getHeight(),
                BufferedImage.TYPE_INT_RGB);
		
        Graphics2D g = _VehicleMask.createGraphics();
        g.drawImage(Vehicle, _CollisionVehicleFilter, 0, 0);
        
        g.dispose();
        
        
        //Create the shape from the image
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
	
	/**
	 * Uses a concept called Axis Aligned minimum bounding box (AABB) collision which gets the images smallest bounding box and checks if it collides with any of those bounding boxes
	 * @param RequestingVehicleLocation The Requesting vehicles X and Y coordinates 
	 * @param RequestingVehicleRotation The Requesting vehicles rotation
	 * @param VehicleLocations All the vehicle locations to check (ensuring itself is not included)
	 * @param VehicleRotations All the vehicle rotations to check (ensuring itself is not included) it should be 1 to 1 with vehicle locations
	 * @return True if there is a collision false if not
	 */
	public boolean CheckVehicleCollision(Point RequestingVehicleLocation, double RequestingVehicleRotation, Point[] VehicleLocations, Double[] VehicleRotations)
	{
		boolean result = false;
		
		//Ensure the collision box is made the first couple of frames this may not exist but generally by the time the game has actually opened it is ready 
		if(_VehicleCollisionBox != null)
		{
			//rotated the bounding box by the rotation for the requesting vehicle
			AffineTransform tx = new AffineTransform();
			tx.rotate(RequestingVehicleRotation, _VehicleCollisionBox.getCenterX(), _VehicleCollisionBox.getCenterY());
			
			//Ensure that the box is cloned otherwise it will rotate from a rotation each frame
			Shape RequestingVehicleCollisionBox = tx.createTransformedShape((Rectangle2D)_VehicleCollisionBox.clone()); 
			for (int i = 0; i < VehicleLocations.length; i++)
			{			
				//Rotate the image for each of the vehicles to check
				tx = new AffineTransform();
				tx.rotate(VehicleRotations[i], _VehicleCollisionBox.getCenterX(), _VehicleCollisionBox.getCenterY());
				
				//Ensure it is cloned
				Shape shape = tx.createTransformedShape((Rectangle2D)_VehicleCollisionBox.clone());
				
				//Check if the vehicles intersect with offsets for reasonably accurate collision system
				if(RequestingVehicleCollisionBox.intersects((VehicleLocations[i].x + 12) - RequestingVehicleLocation.x, (VehicleLocations[i].y + 10) - RequestingVehicleLocation.y, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight()))
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Generates a GeneralPath (Custom Shape) around the image finding the target colour
	 * Using https://stackoverflow.com/questions/7218309/smoothing-a-jagged-path
	 * @param target The target colour to draw around. Black and white as currently it is using a Black or white image
	 * @param bi The image to create the Path off of
	 * @return The GeneralPath shape of the image white being the colour it was looking for black everything else
	 */
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
    
    /**
     * Checks for a collision with the map
     * @param x The x coordinate of the vehicle
     * @param y The y coordinate of the vehicle
     * @return true if it collides false otherwise
     */
    @SuppressWarnings("static-access")
	public boolean CheckForCollision(int x, int y)
    {
    	//Map area will take some time to create due to the size of it so make sure that is done before continuing
    	if (_MapArea == null || _VehicleArea == null)
    		return false;
    	boolean result = false;
    	
    	
    	//Create a iterator so it can check that every point is not colliding
    	PathIterator PI = _MapArea.getPathIterator(null);
    	
    	//Checks if the Vehicle collides with the collision map created of the map
    	//+1 on both due to shrinking of the map for speed causes so offset issues this is to fix them
    	//ONLY CHECKS THE MAP
    	result = _VehicleArea.intersects(PI, (x / _ScaleX) + 1, (y / _ScaleY) + 1, 1, 1);
    	
    	return result; 
    }
    
    /**
     * Scales the image by a multiplier
     * @param sbi The image to scale
     * @param scale The scale in which to use in decimal 0.5 for half 2 for twice the size
     * @return The scaled image
     */
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
    
    /**
     * Rotates the image by degrees
     * @param img The image to rotate
     * @param angle The angle to rotate the image
     * @return The rotated image
     */
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

    /**
     * Checks if the bounding boxes of the labels collide
     * Used for the checkpoint system as they do not need to be accurate
     * @param testa The first label to check
     * @param testb The Label to check against
     * @return true if they intersect otherwise false
     */
    public boolean intersects(JLabel testa, JLabel testb){
        Area areaA = new Area(testa.getBounds());
        Area areaB = new Area(testb.getBounds());

        return areaA.intersects(areaB.getBounds2D());
    }

    /**
     * Closes the collision management system
     */
	public void Close() {
		_MapSW.cancel(true);
		_VehicleSW.cancel(true);
		
	}
}

