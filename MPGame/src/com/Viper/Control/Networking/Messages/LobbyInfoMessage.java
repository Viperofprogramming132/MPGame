package com.Viper.Control.Networking.Messages;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class LobbyInfoMessage extends Message implements Serializable{

	private int _MapIndex;
	
	private ArrayList<Integer> _CurrentPlayers;
	
	public LobbyInfoMessage(MESSAGETYPE type, int PlayerID) {
		super(type, PlayerID);
	}

	public int getMapIndex() {
		return _MapIndex;
	}

	public void setMapIndex(int mapIndex) {
		_MapIndex = mapIndex;
	}

	public ArrayList<Integer> get_CurrentPlayers() {
		return _CurrentPlayers;
	}

	public void set_CurrentPlayers(ArrayList<Integer> _CurrentPlayers) {
		this._CurrentPlayers = _CurrentPlayers;
	}
}
