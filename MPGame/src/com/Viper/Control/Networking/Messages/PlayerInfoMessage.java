package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

public class PlayerInfoMessage extends Message implements Serializable{

	private int _SelectedVehicleIndex;
	
	private boolean _Ready;
	
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

	
}
