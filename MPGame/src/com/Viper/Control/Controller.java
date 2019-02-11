package com.Viper.Control;


import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.Viper.Control.Networking.GameClient;
import com.Viper.Control.Networking.GameServer;
import com.Viper.Sound.SoundController;
import com.Viper.UI.UIControl;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Controller {
	
	private static Controller _instance;
	private UIControl _UIController;
	private int _SelectedMap;
	private Game _GameController;
	private GameClient _Client;
	private GameServer _Server;
	private boolean _Host = false;
	private SoundController _SoundControl;
	
	ArrayList<Player> _Players = new ArrayList<>();
	
	ObservableList<Player> _ObPlayers = FXCollections.observableArrayList(_Players);
	
	public static Controller GetController()
	{
		if (_instance == null)
		{
			_instance = new Controller();
		}
		
		return _instance;
	}
	
	
	private Controller()
	{
		_ObPlayers.addListener(new ListChangeListener<Player>() {

			@Override
			public void onChanged(Change<? extends Player> e) {
				while(e.next())
				{
					if(e.wasAdded())
					{
						_UIController.PlayerConnectedUpdate(e.getAddedSubList().get(0).getName(), true);
					}
				}
			}
		});
		
		_SoundControl = new SoundController();
	}


	public void StartApp() {
		_UIController = UIControl.GetInstance();
		
		_UIController.OpenMainMenu();
		_SoundControl.StartBackgroundMusic();
	}
	
	public void ExitProgram()
	{
		_UIController.dispose();
	}
	
	public void HostGame()
	{
		_UIController.OpenServer();
	}


	public void EnterLobby() {
		if(_Server != null)
			_Host = true;
		
		_UIController.OpenLobby(_Host);
	}
	
	public void StartServer()
	{
		if(_Server == null)
			_Server = new GameServer();
		
		_Server.StartServer(8888);
	}
	
	public boolean ConnectToServer(String address) {
		if(_Client == null)
			_Client = new GameClient();
		
		return _Client.ConnectToServer(address, 8888);
	}


	public Game get_GameController() {
		return _GameController;
	}
	
	public void StartGame() {
		if (_GameController == null)
			_GameController = new Game(_ObPlayers);
		_UIController.OpenGame();
	}
	
	public void SendReady()
	{
		_Client.getLocalPlayer().setReady(true);
	}
	
	public void ConnectToGame(boolean autoConnect)
	{
		_UIController.OpenConnection(autoConnect);
	}
	
	public void addPlayer(Player p) 
	{
		_ObPlayers.add(p);
	}
	
	public ObservableList<Player> getPlayers()
	{
		return _ObPlayers;
	}
	
	public GameClient getClient()
	{
		return _Client;
	}


	public int get_SelectedMap() {
		return _SelectedMap;
	}
	
	public void set_SelectedMap(int Index)
	{
		_SelectedMap = Index;
	}
	
	public ObservableList<Player> getObPlayers()
	{
		return _ObPlayers;
	}
	
	public void addChatMessage(String message)
	{
		_UIController.AddChatMessage(message);
	}


	public void UpdatePlayer(String text) {
		_Client.getLocalPlayer().setName(text);
		
		_Client.PlayerUpdate(_Client.getLocalPlayer().getSpriteIndex());		
	}
	
	public void SendGameStart()
	{
		_Client.StartGame();
	}


	public void Disconnect() {
		
		if (_Client != null)
		{
			_Client.TryCloseCurrentConnection(true);
		}
		//Ensure the Client Disconnect has been processed before shutting the server
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(_Server != null)
		{
			_Server.StopServer();
		}
		
	}


	public void ServerClosed() {
		JOptionPane.showMessageDialog(_UIController, "Server Was Closed Returning to the Main Menu", "Server Closed", JOptionPane.WARNING_MESSAGE);
		
		_UIController.OpenMainMenu();
	}
	
}
