package com.Viper.Model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.omg.CORBA._IDLTypeStub;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.GameClient;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.VehicleUpdateMessage;
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
	
	
	private void SendVehicleUpdateMessage()
	{
		if (_Client == null)
			_Client = Controller.GetController().getClient();
		
		VehicleUpdateMessage message = new VehicleUpdateMessage(MESSAGETYPE.INGAMEPOSUPDATE, _Player.getID());
		
		message.set_Angle(_Angle);
		message.set_CarImageIndex(_CarImageIndex);
		message.set_OnRoughTerrain(_OnRoughTerrain);
		message.set_Speed(_Speed);
		message.setAccelerating(isAccelerating);
		message.set_X(_VehicleLabel.getX());
		message.set_Y(_VehicleLabel.getY());
		
		_Client.SendVehicleUpdate(message);
	}
	
	public void Step()
	{
		if(!_Player.isRemotePlayer())
		{
			UpdateSpeed();
			CalculateNewLocation();
			SendVehicleUpdateMessage();
		}
	}
	
	private void CalculateNewLocation() {
		Point location = _VehicleLabel.getLocation();
		double y = (_Speed / 2) * Math.sin(_Angle);
	    double x = (_Speed / 2 ) * Math.cos(_Angle);
	    
	    location.x += x;
	    location.y += y;
	    
	    _VehicleLabel.setLocation(location);
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
				_Speed *= 1.4d;
				isAccelerating = true;
			}
			else if (_Speed < speedLimit * 0.75 )
			{
				_Speed *= 1.3d;
				isAccelerating = true;
			}
			else
			{
				_Speed *= 1.1d;
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
		
		if(_Speed <= 0.05)
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
		
	}
}
