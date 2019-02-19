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
	
	protected double _Speed;
	
	protected double _Angle;
	
	protected GameClient _Client;
	
	protected boolean isAccelerating = false;
	
	protected boolean _OnRoughTerrain = false;
	
	protected int _CarImageIndex = 0;
	
	protected Player _Player;
	
	protected InGameLabel _VehicleLabel;
	
	protected boolean _forward = false;
	
	protected boolean _backwards = false;
	
	private Point location;
	
	private CollisionManagment _CM;
	
	private InGame _CurrentGame;
	
	private int _CurrentCheckpointDes = 0;
	
	private int _Lap = 0;
	
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

	private void UpdateLocation() {
		
		_VehicleLabel.setLocation(location);
	}

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
	    
	    //Check if it would collide with object if it does dont move
	    if (!_CM.CheckForCollision(localLocation.x, localLocation.y) && !_CM.CheckVehicleCollision(localLocation, _Angle, GetAllLocations((ArrayList<InGameLabel>)_CurrentGame.getVehicleLabels().clone()), GetAllRotations((ArrayList<InGameLabel>)_CurrentGame.getVehicleLabels().clone())))
	    {
	    	location = localLocation;
	    }
	    else
	    {
	    	//Reset speed back to just starting speed so that you have to re accelerate 
	    	_Speed = 5;
	    	Controller.GetController().PlayerCrashSound();
	    }
	}
	
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

	private void UpdateSpeed()
	{
		if(!_forward && !_backwards)
			SlowDown();
		
		if(_forward)
			Accelerate(false);
		
		if(_backwards)
			Accelerate(true);

	}

	private void Accelerate(boolean reverse) {
		int speedLimit = 50;
		if (_Speed < 0 && !reverse)
		{
			_Speed = 0;
		}
		
		if(_OnRoughTerrain)
			speedLimit = speedLimit / 2;
		
		if (_Speed == speedLimit)
		{
			isAccelerating = false;
			return;
		}
		
		if (reverse)
		{
			speedLimit *= -1;
		}
		
		if (_Speed < speedLimit)
		{
			if (_Speed == 0)
			{
				if(!reverse)
					_Speed = 5d;
				else
					_Speed = -5d;
				isAccelerating = true;
			}
			else if (_Speed < speedLimit * 0.25)
			{
				_Speed *= 1.05d;
				isAccelerating = true;
			}
			else if (_Speed < speedLimit * 0.75 )
			{
				_Speed *= 1.02d;
				isAccelerating = true;
			}
			else
			{
				_Speed *= 1.01d;
				isAccelerating = true;
			}
			
			if (_Speed > speedLimit)
			{
				_Speed = speedLimit;
				isAccelerating = false;
			}
		}
		
		
        if(_Speed > speedLimit)
        {
        	isAccelerating = false;
            SlowDown();
        }
	}

	private void SlowDown() {
		isAccelerating = false;
		
		if(_Speed <= 0.3)
		{
			_Speed = 0;
		}
		else
		{
			_Speed *= 0.9d;
		}
	}
	
	public void setAngle(double angle)
	{
		_Angle = angle;
	}

	public boolean is_forward() {
		return _forward;
	}

	public void set_forward(boolean _forward) {
		this._forward = _forward;
	}

	public boolean is_backwards() {
		return _backwards;
	}

	public void set_backwards(boolean _backwards) {
		this._backwards = _backwards;
	}
	
	public void set_VehicleLabel(InGameLabel display)
	{
		_VehicleLabel = display;
	}
	
	public Image ReadVehicleImage(int startImage) throws IOException
	{
		File f = new File("src/imgs/vehicles");
		
		BufferedImage img = ImageIO.read(f.listFiles()[startImage]);
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	public void setPlayer(Player p)
	{
		_Player = p;
	}
	
	public Player getPlayer()
	{
		return _Player;
	}

	public double getAngle() {
		return _Angle;
	}
	
	public void SelfUpdate()
	{
		if (_Client == null)
			_Client = Controller.GetController().getClient();
		
		_Client.NotifyOfUpdates(this);
		_Client.StartListening();
	}
	
	public void VehicleUpdate(VehicleUpdateMessage msg)
	{
		_Angle = msg.get_Angle();
		_VehicleLabel.setLocation(msg.get_X(), msg.get_Y());
		_Lap = msg.get_Lap();
	}
	
	public void setCurrentGame(InGame game)
	{
		_CurrentGame = game;
	}
	
	public int get_Lap()
	{
		return _Lap;
	}
}
