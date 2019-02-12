package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PlayerInfoMessage extends Message implements Serializable{

	private int _SelectedVehicleIndex;
	
	private boolean _Ready;
	
	private String _Name;
	
	public PlayerInfoMessage(MESSAGETYPE type, int PlayerID) {
		super(type, PlayerID);
	}

	public int get_SelectedVehicleIndex() {
		return _SelectedVehicleIndex;
	}

	public void set_SelectedVehicleIndex(int _SelectedVehicleIndex) {
		this._SelectedVehicleIndex = _SelectedVehicleIndex;
	}

	public boolean is_Ready() {
		return _Ready;
	}

	public void set_Ready(boolean _Ready) {
		this._Ready = _Ready;
	}

	public String get_Name() {
		return _Name;
	}

	public void set_Name(String _Name) {
		this._Name = _Name;
	}

	
}
