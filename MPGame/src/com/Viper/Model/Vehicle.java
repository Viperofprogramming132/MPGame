package com.Viper.Model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.Viper.Control.CollisionManagment;
import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.GameClient;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.VehicleUpdateMessage;
import com.Viper.UI.InGame.Checkpoint;
import com.Viper.UI.InGame.InGame;
import com.Viper.UI.InGame.InGameLabel;

public class Vehicle 
{
	/**
	 * The current speed in which the vehicle is travelling
	 */
	private double _Speed;
	
	/**
	 * The angle in which the vehicle is turned towards
	 */
	private double _Angle;
	
	/**
	 * The client the communicates with the server
	 */
	private GameClient _Client;
	
	/**
	 * The Player that this vehicle is controlled by
	 */
	private Player _Player;
	
	/**
	 * The Label (Sprite) that this vehicle logic controls
	 */
	private InGameLabel _VehicleLabel;
	
	/**
	 * If the vehicle should be moving forward
	 */
	private boolean _forward = false;
	
	/**
	 * If the vehicle should be moving backwards
	 */
	private boolean _backwards = false;
	
	/**
	 * The location of the vehicle
	 */
	private Point _Location;
	
	/**
	 * The collision Management system that stops movement off the track and into other people
	 * Also deals with checkpoint crossing
	 */
	private CollisionManagment _CM;
	
	/**
	 * The game screen that this vehicle is drawn on
	 */
	private InGame _CurrentGame;
	
	/**
	 * The current checkpoint destination stopping users going over the same checkpoint repeatedly
	 */
	private int _CurrentCheckpointDes = 0;
	
	/**
	 * The current lap number. Starts a 0 as when parsing the line it increases by 1
	 */
	private int _Lap = 0;
	
	/**
	 * Sends a vehicle update message to the client to send to the server
	 */
	private void SendVehicleUpdateMessage()
	{
		if (_Client == null)
			_Client = Controller.GetController().getClient();
		
		VehicleUpdateMessage message = new VehicleUpdateMessage(MESSAGETYPE.INGAMEPOSUPDATE, _Player.getID());
		
		message.set_Angle(_Angle);
		message.set_X(_VehicleLabel.getX());
		message.set_Y(_VehicleLabel.getY());
		message.set_Lap(_Lap);
		
		_Client.SendVehicleUpdate(message);
	}
	
	/**
	 * Attempts to step the vehicle
	 * 
	 * Updates the speed of the vehicle
	 * Calculates where the player would move to with that speed and if it doesn't collide set that location as the new location
	 * Updates the new location
	 * Sends the update out
	 * Checks if the user has won the game
	 */
	public void Step()
	{
		if(_CM == null)
		{
			_CM = Controller.GetController().get_GameController().get_CM();
		}
		if(!_Player.isRemotePlayer())
		{
			UpdateSpeed();
			CalculateNewLocation();
			
			CheckIfPassedCheckpoint();
			
			UpdateLocation();
			
			SendVehicleUpdateMessage();
			
			CheckForWin();
		}
	}
	
	/**
	 * Checks if the user has achieved over 3 laps if so they have won the game
	 */
	private void CheckForWin() {
		for(InGameLabel v  : _CurrentGame.getVehicleLabels())
		{
			if(v.get_Vehicle().get_Lap() > 3)
			{
				Controller.GetController().OpenMessagePane(v.get_Vehicle().getPlayer().getName() + " has won the game! Closing the game", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				
				System.exit(0);
			}
		}
	}

	/**
	 * Checks if the vehicle is overlapping with the checkpoint that is the vehicles current destination  
	 */
	private void CheckIfPassedCheckpoint() {
		ArrayList<Checkpoint> cps = _CurrentGame.get_CheckPointLabels();
		
		if(_CM.intersects(cps.get(_CurrentCheckpointDes), _VehicleLabel))
		{
			_CurrentCheckpointDes++;
			
			if(_CurrentCheckpointDes > cps.size() - 1)
			{
				_CurrentCheckpointDes = 0;
			}
			else
			{
				_Lap++;
			}
		}
	}

	/**
	 * Sets the location of the vehicle label (The actual sprite) to the location that was calculated
	 */
	private void UpdateLocation() {
		
		_VehicleLabel.setLocation(_Location);
	}

	/**
	 * Gets where the vehicle should move to before moving the vehicle it checks for a collision at this location and does not allow the location to be updated if there would be a collision
	 */
	@SuppressWarnings("unchecked")
	private void CalculateNewLocation() {
		Point localLocation = _VehicleLabel.getLocation();
		double y = (_Speed / 2) * Math.sin(_Angle);
	    double x = (_Speed / 2 ) * Math.cos(_Angle);
	    
	    localLocation.x += x;
	    localLocation.y += y;
	    
	    //Create a Buffered Image of the Label used for collision
	    BufferedImage temp = new BufferedImage(
	    		_VehicleLabel.getIcon().getIconWidth(),
	    		_VehicleLabel.getIcon().getIconHeight(),
	    		BufferedImage.TYPE_INT_ARGB);
	    Graphics g = temp.getGraphics();
	    
	    _VehicleLabel.getIcon().paintIcon(null, g, 0, 0);
	    
	    _CM.CreateVehicleMask(temp, _Angle);
	    
	    //Check if it would collide with object if it does, don't move
	    if (!_CM.CheckForCollision(localLocation.x, localLocation.y) && !_CM.CheckVehicleCollision(localLocation, _Angle, GetAllLocations((ArrayList<InGameLabel>)_CurrentGame.getVehicleLabels().clone()), GetAllRotations((ArrayList<InGameLabel>)_CurrentGame.getVehicleLabels().clone())))
	    {
	    	_Location = localLocation;
	    }
	    else
	    {
	    	//Reset speed back to just starting speed so that you have to re accelerate 
	    	_Speed = 5;
	    	Controller.GetController().PlayerCrashSound();
	    }
	}
	
	/**
	 * Gets the location of all the other vehicle labels from the game
	 * @param vehicleLabels The list of the vehicle labels to get the locations from
	 * @return Point array of the locations
	 */
	private Point[] GetAllLocations(ArrayList<InGameLabel> vehicleLabels)
	{
		ArrayList<Point> locations = new ArrayList<>();
		
		vehicleLabels.remove(_VehicleLabel);
		
		for (InGameLabel igl : vehicleLabels)
		{
			locations.add(igl.getLocation());
		}
		
		return locations.toArray(new Point[locations.size()]);
	}
	
	/**
	 * Gets the angle of all the other vehicle labels from the game
	 * @param vehicleLabels The list of the vehicle labels to get the angle from
	 * @return Double array of the rotations
	 */
	private Double[] GetAllRotations(ArrayList<InGameLabel> vehicleLabels)
	{
		ArrayList<Double> angles = new ArrayList<>();
		
		vehicleLabels.remove(_VehicleLabel);
		
		for (InGameLabel igl : vehicleLabels)
		{
			angles.add(igl.get_Vehicle().getAngle());
		}
		
		return angles.toArray(new Double[angles.size()]);
	}

	/**
	 * Checks if the users wants to move forward or backwards
	 * If both are held should slow down the vehicle
	 */
	private void UpdateSpeed()
	{
		if(!_forward && !_backwards)
			SlowDown();
		else if(_forward && !_backwards)
			Accelerate(false);
		else if(_backwards && !_forward)
			Accelerate(true);

	}

	/**
	 * Sets a speed limit to 50 Pixels per frame
	 * If forward then move up to that speed limit increasing accelerating slower as speed increases
	 * If backwards then move back 5 pixels per frame
	 * @param reverse If the vehicle should reverse
	 */
	private void Accelerate(boolean reverse) {
		int speedLimit = 50;
		if (_Speed < 0 && !reverse)
		{
			_Speed = 0;
		}
		
		if (_Speed == speedLimit)
		{
			return;
		}
		
		if (reverse)
		{
			speedLimit = -5;
			
			_Speed = -5;
			
	        if(_Speed < speedLimit)
	        {
	            SlowDown();
	        }
		}
		else
		{
			if (_Speed < speedLimit)
			{
				if (_Speed == 0)
				{
					if(!reverse)
						_Speed = 5d;
					else
						_Speed = -5d;
				}
				else if (_Speed < speedLimit * 0.25)
				{
					_Speed *= 1.05d;
				}
				else if (_Speed < speedLimit * 0.75 )
				{
					_Speed *= 1.02d;
				}
				else
				{
					_Speed *= 1.01d;
				}
				
				if (_Speed > speedLimit)
				{
					_Speed = speedLimit;
				}
			}
			
			
			
	        if(_Speed > speedLimit)
	        {
	            SlowDown();
	        }
		}
	}

	/**
	 * Slows down the vehicle or stops the vehicle over time
	 */
	private void SlowDown() {
		if(_Speed <= 0.3)
		{
			_Speed = 0;
		}
		else
		{
			_Speed *= 0.9d;
		}
	}
	
	/**
	 * Sets the angle to the given double
	 * @param angle The angle to set
	 */
	public void setAngle(double angle)
	{
		_Angle = angle;
	}

	/**
	 * Is the vehicle attempting to go forward
	 * @return If vehicle is attempting to move forward
	 */
	public boolean is_forward() {
		return _forward;
	}

	/**
	 * Set whether the vehicle is attempting to move forward
	 * @param _forward The value to set forward to
	 */
	public void set_forward(boolean _forward) {
		this._forward = _forward;
	}

	/**
	 * Is the vehicle attempting to go backwards
	 * @return If vehicle is attempting to move backwards
	 */
	public boolean is_backwards() {
		return _backwards;
	}

	/**
	 * Set whether the vehicle is attempting to move backwards
	 * @param _forward The value to set backwards to
	 */
	public void set_backwards(boolean _backwards) {
		this._backwards = _backwards;
	}
	
	/**
	 * Sets the vehicle label (Sprite) to the given value
	 * @param display The label to set it to
	 */
	public void set_VehicleLabel(InGameLabel display)
	{
		_VehicleLabel = display;
	}
	
	/**
	 * Gets the vehicle image from the file
	 * @param startImage The vehicle image index to read
	 * @return The image that was read
	 * @throws IOException If the file fails to be found or fails to open
	 */
	public Image ReadVehicleImage(int startImage) throws IOException
	{
		ArrayList<String> pos = Controller.GetController().PopulateVehicleSelector();
		
		BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream("/imgs/vehicles/" + pos.get(startImage)));
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	/**
	 * Sets the player that controls this vehicle
	 * @param p The player that controls this vehicle
	 */
	public void setPlayer(Player p)
	{
		_Player = p;
	}
	
	/**
	 * @return The player that controls this vehicle
	 */
	public Player getPlayer()
	{
		return _Player;
	}

	/**
	 * Gets the angle that the vehicle is at
	 * @return The angle the vehicle is currently meant to be facing
	 */
	public double getAngle() {
		return _Angle;
	}
	
	/**
	 * Starts the listening thread for the game client for UDP vehicle updates
	 */
	public void SelfUpdate()
	{
		if (_Client == null)
			_Client = Controller.GetController().getClient();
		
		_Client.NotifyOfUpdates(this);
		_Client.StartListening();
	}
	
	/**
	 * Updates all the vehicle information based off the message received from the server
	 * @param msg The message to use the information form
	 */
	public void VehicleUpdate(VehicleUpdateMessage msg)
	{
		_Angle = msg.get_Angle();
		_VehicleLabel.setLocation(msg.get_X(), msg.get_Y());
		_Lap = msg.get_Lap();
	}
	
	/**
	 * Sets the current game screen that is being played
	 * @param game
	 */
	public void setCurrentGame(InGame game)
	{
		_CurrentGame = game;
	}
	
	/**
	 * Gets the current lap number the vehicle is on
	 * @return
	 */
	public int get_Lap()
	{
		return _Lap;
	}
}
