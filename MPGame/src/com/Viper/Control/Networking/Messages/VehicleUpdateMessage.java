package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

import com.Viper.Control.Networking.GameClient;

public class VehicleUpdateMessage extends Message implements Serializable{

	private double _Speed;
	
	private double _Angle;
	
	private boolean isAccelerating = false;
	
	private boolean _OnRoughTerrain = false;
	
	private int _CarImageIndex = 0;
	
	private int _X;
	
	private int _Y;
	
	public VehicleUpdateMessage(MESSAGETYPE type, int playerID) {
		super(type, playerID);
		
	}

	public int get_CarImageIndex() {
		return _CarImageIndex;
	}

	public void set_CarImageIndex(int _CarImageIndex) {
		this._CarImageIndex = _CarImageIndex;
	}

	public boolean is_OnRoughTerrain() {
		return _OnRoughTerrain;
	}

	public void set_OnRoughTerrain(boolean _OnRoughTerrain) {
		this._OnRoughTerrain = _OnRoughTerrain;
	}

	public boolean isAccelerating() {
		return isAccelerating;
	}

	public void setAccelerating(boolean isAccelerating) {
		this.isAccelerating = isAccelerating;
	}

	public double get_Angle() {
		return _Angle;
	}

	public void set_Angle(double _Angle) {
		this._Angle = _Angle;
	}

	public double get_Speed() {
		return _Speed;
	}

	public void set_Speed(double _Speed) {
		this._Speed = _Speed;
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

}
