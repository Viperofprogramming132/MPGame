package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

/**
 * An extension of Message that sends all the information about that player
 * @author Aidan
 *
 */
public class PlayerInfoMessage extends Message implements Serializable{

	/**
	 * The serial ID so the message can be identified either end
	 */
	private static final long serialVersionUID = 6398098486434103187L;

	/**
	 * The vehicle index that the player has selected
	 */
	private int _SelectedVehicleIndex;
	
	/**
	 * If the player is ready or not
	 */
	private boolean _Ready;
	
	/**
	 * The name of the player
	 */
	private String _Name;
	
	/**
	 * Creates a Player info message that will be used to update other peoples instance of the player at the specified ID
	 * @param type The type of message that it is. Should be MESSAGETYPE.PLAYERINFO
	 * @param PlayerID The ID of the player that sent the message
	 */
	public PlayerInfoMessage(MESSAGETYPE type, int PlayerID) {
		super(type, PlayerID);
	}

	/**
	 * Gets the Selected vehicle
	 * @return The Index of the selected vehicle
	 */
	public int get_SelectedVehicleIndex() {
		return _SelectedVehicleIndex;
	}

	/**
	 * Sets the selected vehicle index
	 * @param _SelectedVehicleIndex The index to set it to
	 */
	public void set_SelectedVehicleIndex(int _SelectedVehicleIndex) {
		this._SelectedVehicleIndex = _SelectedVehicleIndex;
	}

	/**
	 * If the player is ready
	 * @return If the player is ready or not
	 */
	public boolean is_Ready() {
		return _Ready;
	}

	/**
	 * Sets ready to the given state
	 * @param _Ready The state to set
	 */
	public void set_Ready(boolean _Ready) {
		this._Ready = _Ready;
	}

	/**
	 * @return The name of the player
	 */
	public String get_Name() {
		return _Name;
	}

	/**
	 * The name to set the player to
	 * @param _Name
	 */
	public void set_Name(String _Name) {
		this._Name = _Name;
	}

	
}
