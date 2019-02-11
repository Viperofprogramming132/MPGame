package com.Viper.Control;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.Viper.Control.Networking.GameClient;
import com.Viper.Control.Networking.Session;
import com.Viper.Model.Vehicle;
import com.Viper.UI.InGame.InGameLabel;

public class Player {

	private int _ID;
	private String _Name;
	private Session _ClientSession;
	private InGameLabel _Sprite;
	private Vehicle _VehicleLogic;
	private boolean _Remote;
	private int x;
	private int y;
	private int _SpriteIndex = 0;
	private boolean _Ready = false;
	private GameClient _Client;
	
	public Player(int ID, boolean remote)
	{
		_ID = ID;
		set_RemotePlayer(remote);
	}
	
	public Player(int ID, boolean remote, GameClient client)
	{
		_ID = ID;
		set_RemotePlayer(remote);
		_Client = client;
	}

	public int getID() {
		return _ID;
	}

	public String getName() {
		return _Name;
	}

	public void setName(String _Name) {
		this._Name = _Name;
	}

	public Session getClientSession() {
		return _ClientSession;
	}

	public void setClientSession(Session _ClientSession) {
		this._ClientSession = _ClientSession;
	}

	public InGameLabel getSprite() {
		return _Sprite;
	}

	public void setSprite(InGameLabel _Sprite) {
		this._Sprite = _Sprite;
	}

	public boolean isRemotePlayer() {
		return _Remote;
	}

	public void set_RemotePlayer(boolean _Remote) {
		this._Remote = _Remote;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Vehicle get_VehicleLogic() {
		return _VehicleLogic;
	}

	public void set_VehicleLogic(Vehicle _VehicleLogic) {
		this._VehicleLogic = _VehicleLogic;
	}

	public boolean isReady() {
		return _Ready;
	}
	
	public void setReady(boolean Ready) {
		this._Ready = Ready;
		if (_Client != null)
			_Client.PlayerUpdate(_SpriteIndex);
	}
	
	public int getSpriteIndex()
	{
		return _SpriteIndex;
	}
	
	public void setSpriteIndex(int index) {
		_SpriteIndex = index;
	}

}
