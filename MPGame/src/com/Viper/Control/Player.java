package com.Viper.Control;

import com.Viper.Control.Networking.GameClient;
import com.Viper.Model.Vehicle;
import com.Viper.UI.InGame.InGameLabel;

/**
 * Player class contains all the information about each of the players
 * @author Aidan
 *
 */
public class Player {

	/**
	 * Unique identifier used when sending packets across so the program can find which user sent it easily
	 */
	private int _ID;
	
	/**
	 * The persons name default being "Player " + _ID but can be changed in the menu
	 */
	private String _Name;

	/**
	 * The Vehicle sprite itself
	 * JLabel that does all the displaying of the vehicles
	 */
	private InGameLabel _Sprite;
	
	/**
	 * The logic for the vehicle
	 */
	private Vehicle _VehicleLogic;
	
	/**
	 * If the user is the application that is running the server this is true otherwise false 
	 */
	private boolean _Remote;
	
	/**
	 * The index of the sprite the player is using
	 */
	private int _SpriteIndex = 0;
	
	/**
	 * If the user is ready
	 */
	private boolean _Ready = false;
	
	/**
	 * The Client for the client side of the application
	 */
	private GameClient _Client;
	
	/**
	 * Constructs a player
	 * @param ID The unique identifier assigned to the player
	 * @param remote If the player is remote
	 */
	public Player(int ID, boolean remote)
	{
		_ID = ID;
		_Remote = remote;
	}
	
	/**
	 * Constructs a player (Only use this one if it is the local player)
	 * @param ID The unique identifier assigned to the player
	 * @param remote If the player is remote
	 * @param client The game client that is assigned to the player
	 */
	public Player(int ID, boolean remote, GameClient client)
	{
		_ID = ID;
		_Remote = remote;
		_Client = client;
	}

	/**
	 * Gets the unique ID
	 * @return The ID of the player
	 */
	public int getID() {
		return _ID;
	}

	/**
	 * Gets the name of the player
	 * @return The name of the player
	 */
	public String getName() {
		return _Name;
	}

	/**
	 * Sets the players name
	 * @param _Name The name to set
	 */
	public void setName(String _Name) {
		this._Name = _Name;
	}

	/**
	 * Gets the sprite label
	 * @return The label that draws this players sprite
	 */
	public InGameLabel getSprite() {
		return _Sprite;
	}

	/**
	 * Sets the sprite label
	 * @param _Sprite The label to set to
	 */
	public void setSprite(InGameLabel Sprite) {
		this._Sprite = Sprite;
	}

	/**
	 * Gets if the player is a remote player
	 * @return True if the player is the host otherwise false. Always false if its for the server 
	 */
	public boolean isRemotePlayer() {
		return _Remote;
	}

	/**
	 * Gets the vehicle logic
	 * @return The logic for player
	 */
	public Vehicle get_VehicleLogic() {
		return _VehicleLogic;
	}

	/**
	 * Sets the logic to the given one
	 * @param _VehicleLogic The logic to set this players vehicle logic to
	 */
	public void set_VehicleLogic(Vehicle _VehicleLogic) {
		this._VehicleLogic = _VehicleLogic;
	}

	/**
	 * Gets if the player is ready
	 * @return True if the player is ready otherwise false
	 */
	public boolean isReady() {
		return _Ready;
	}
	
	/**
	 * Sets the users ready state to the opposite and broadcasts it to the other clients if the user is the local one
	 */
	public void setReady() {
		this._Ready = !_Ready;
		if (_Client != null)
			_Client.PlayerUpdate(_SpriteIndex);
	}
	
	/**
	 * Just sets the ready state to a specific state
	 * @param ready The state to set ready to
	 */
	public void setReady(boolean ready) {
		this._Ready = ready;
	}
	
	/**
	 * Gets the Sprite Index
	 * @return The index of the selected Sprite by this player
	 */
	public int getSpriteIndex()
	{
		return _SpriteIndex;
	}
	
	/**
	 * Sets the sprite index
	 * @param index The index to set the players sprite to
	 */
	public void setSpriteIndex(int index) {
		_SpriteIndex = index;
	}
	
	/**
	 * Override the ToString so that the players name and if they are ready or not is displayed in the lobby
	 */
	@Override
	public String toString()
	{
		if (_Ready)
			return _Name + " " + "Ready";
		else
			return _Name + " " + "Not Ready";
	}

}
