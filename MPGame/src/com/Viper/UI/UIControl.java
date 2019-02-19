package com.Viper.UI;

import javax.swing.*;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.UI.InGame.InGame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class UIControl extends JFrame implements ActionListener {

	private static UIControl _instance;
	
	private final ArrayList<JPanel> _Screens;
	private final Container _ContentContainer;
	
	private MainMenu _MainMenuScreen;
	private InGame _InGameScreen;
	private Lobby _LobbyScreen;
	private Server _ServerScreen;
	private Connection _ConnectionScreen;
	
	public static UIControl GetInstance()
	{
		if(_instance == null)
		{
			_instance = new UIControl();
		}
		
		return _instance;
	}
	
	private UIControl()
	{
		setTitle("Multiplayer Game");
		setResizable(false);
		setBounds(0,0,1500,1000);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		_Screens = new ArrayList<JPanel>();
		_ContentContainer = getContentPane();
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				windowClosed(e);
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				Controller.GetController().Disconnect(true);
				System.exit(0);
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void OpenMainMenu() {
		HideAllScreens();
		if (_MainMenuScreen == null)
		{
			_MainMenuScreen = new MainMenu();
			_Screens.add(_MainMenuScreen);
			_ContentContainer.add(_MainMenuScreen);
		}
		
		this.setSize(400, 320);
		_MainMenuScreen.setVisible(true);
		this.setVisible(true);
		
	}

	private void HideAllScreens() {
		_Screens.stream().forEach(s -> s.setVisible(false));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void OpenServer() {
		HideAllScreens();
		if(_ServerScreen == null)
		{
			_ServerScreen = new Server();
			_Screens.add(_ServerScreen);
			_ContentContainer.add(_ServerScreen);
		}
		
		this.setSize(600,600);
		_ServerScreen.setVisible(true);
	}

	public void OpenLobby(boolean host) {
		
		HideAllScreens();
		if(_LobbyScreen == null)
		{
			_LobbyScreen = new Lobby(host);
			_Screens.add(_LobbyScreen);
			_ContentContainer.add(_LobbyScreen);
		}
		
		this.setSize(1500,600);
		_LobbyScreen.setVisible(true);
		
	}

	public void OpenGame() {
		
		HideAllScreens();		
		if(_InGameScreen == null)
		{
			_InGameScreen = new InGame(Controller.GetController().get_GameController().GetLocalPlayer());
			_Screens.add(_InGameScreen);
			_ContentContainer.add(_InGameScreen);
		}
		
		;
		this.setSize(1500,1000);
		_InGameScreen.setVisible(true);
	}

	public void UpdateIngameScreen() {
		if (_InGameScreen != null)
			_InGameScreen.Frame();
		
	}
	
	public void OpenConnection(boolean autoConnect)
	{
		HideAllScreens();
		if(!autoConnect)
		{
			if(_ConnectionScreen == null)
			{
				_ConnectionScreen = new Connection(autoConnect);
				_Screens.add(_ConnectionScreen);
				_ContentContainer.add(_ConnectionScreen);
			}
			this.setSize(300,230);
			_ConnectionScreen.setVisible(true);
		}
		else
		{
			_ConnectionScreen = new Connection(autoConnect);
		}
	}

	public void PlayerConnectedUpdate(Player player, boolean connected)
	{
		if(_LobbyScreen != null)
		{
			if(connected)
			{
				_LobbyScreen.AppendChat("Player has joined the Game: " + player.getName());
				_LobbyScreen.UpdateList(player);
			}
			else
			{
				_LobbyScreen.AppendChat("Player has left the Game: " + player);
				_LobbyScreen.UpdateList(player);
			}
		}
	}
	
	public void AddChatMessage(String message)
	{
		_LobbyScreen.AppendChat(message);
	}

	public void OpenMessagePane(String text, String windowName, int MessageType ) {
		JOptionPane.showMessageDialog(this, text, windowName, MessageType);
	}
	public int OpenOptionsPanel(String text)
	{
		return JOptionPane.showConfirmDialog(this, text);
	}

	public void PlayerNameUpdate(Player player) {
		_LobbyScreen.AppendChat("A player has Changed their name to " + player.getName());
		_LobbyScreen.UpdateList(player);
	}
}
