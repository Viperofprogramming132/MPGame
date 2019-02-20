package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

/**
 * An extension of message that contains that angle, lap and x, y coordinates of the vehicle
 * @author Aidan
 *
 */
public class VehicleUpdateMessage extends Message implements Serializable{
	
	/**
	 * The serial ID so the message can be identified either end
	 */
	private static final long serialVersionUID = -7664237747775579542L;

	/**
	 * The angle in which the vehicle is facing
	 */
	private double _Angle;
	
	/**
	 * The X coordinate of the vehicle
	 */
	private int _X;
	
	/**
	 * The Y coordinate of the vehicle
	 */
	private int _Y;
	
	/**
	 * The current lap the vehicle is on
	 */
	private int _Lap;
	
	/**
	 * Creates a vehicle update message for the specified player
	 * @param type The type of the message. Should be MESSAGETYPE.INGAMEPOSUPDATE
	 * @param playerID The ID of the player that sent the message
	 */
	public VehicleUpdateMessage(MESSAGETYPE type, int playerID) {
		super(type, playerID);
	}

	/**
	 * @return The angle in which the vehicle is facing
	 */
	public double get_Angle() {
		return _Angle;
	}

	/**
	 * Sets the angle the vehicle is facing
	 * @param _Angle The angle that the vehicle is facing
	 */
	public void set_Angle(double _Angle) {
		this._Angle = _Angle;
	}
	
	/** 
	 * @return The X coordinate of the vehicle
	 */
	public int get_X() {
		return _X;
	}

	/**
	 * Sets the X coordinate to the specified number
	 * @param _X The number to set the X coordinate to
	 */
	public void set_X(int _X) {
		this._X = _X;
	}
	
	/** 
	 * @return The Y coordinate of the vehicle
	 */
	public int get_Y() {
		return _Y;
	}

	 /**
	 * Sets the Y coordinate to the specified number
	 * @param _Y The number to set the Y coordinate to
	 */
	public void set_Y(int _Y) {
		this._Y = _Y;
	}

	/**
	 * Set which lap the vehicle is currently on
	 * @param _Lap The lap in which the vehicle is currently doing
	 */
	public void set_Lap(int _Lap) {
		this._Lap = _Lap;
	}
	
	/**
	 * @return The lap number the user is completing
	 */
	public int get_Lap()
	{
		return _Lap;
	}

}
