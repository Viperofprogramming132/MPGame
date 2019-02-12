package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

public class VehicleUpdateMessage extends Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7664237747775579542L;

	private double _Angle;
	
	private int _X;
	
	private int _Y;
	
	private int _Lap;
	
	public VehicleUpdateMessage(MESSAGETYPE type, int playerID) {
		super(type, playerID);
		
	}

	public double get_Angle() {
		return _Angle;
	}

	public void set_Angle(double _Angle) {
		this._Angle = _Angle;
	}


	public int get_X() {
		return _X;
	}

	public void set_X(int _X) {
		this._X = _X;
	}

	public int get_Y() {
		return _Y;
	}

	public void set_Y(int _Y) {
		this._Y = _Y;
	}

	public void set_Lap(int _Lap) {
		this._Lap = _Lap;
	}
	
	public int get_Lap()
	{
		return _Lap;
	}

}
