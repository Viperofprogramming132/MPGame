package com.Viper.Control.Networking.Messages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An extension of Message that contains a list of the player IDs and the selected map index for the server that is running
 * @author Aidan
 *
 */
public class LobbyInfoMessage extends Message implements Serializable{

	/**
	 * The serial ID so the message can be identified either end
	 */
	private static final long serialVersionUID = 2297306871534525781L;

	/**
	 * The index of the selected map
	 */
	private int _MapIndex;
	
	/**
	 * The list of player IDs
	 */
	private ArrayList<Integer> _CurrentPlayers;
	
	/**
	 * Creates a instance of LobbyInfoMessage
	 * @param type The type of the message. Should be MESSAGETYPE.LOBBYINFO
	 * @param PlayerID
	 */
	public LobbyInfoMessage(MESSAGETYPE type, int PlayerID) {
		super(type, PlayerID);
	}

	/**
	 * @return The index of the selected map
	 */
	public int getMapIndex() {
		return _MapIndex;
	}

	/**
	 * Sets the map index to the specified number
	 * @param mapIndex The map index to be set
	 */
	public void setMapIndex(int mapIndex) {
		_MapIndex = mapIndex;
	}

	/**
	 * @return The list of the player IDs
	 */
	public ArrayList<Integer> get_CurrentPlayers() {
		return _CurrentPlayers;
	}

	/**
	 * Sets the list of player IDs to the given list
	 * @param _CurrentPlayers The list to set the list of IDs to
	 */
	public void set_CurrentPlayers(ArrayList<Integer> _CurrentPlayers) {
		this._CurrentPlayers = _CurrentPlayers;
	}
}
