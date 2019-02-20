package com.Viper.UI;

import javax.swing.*;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.UI.InGame.InGame;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

/**
 * UIControl is the frame that all the panels
 * 
 * Deals with communication between the controller and the UI elements
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class UIControl extends JFrame {

	/**
	 * Stop there being more that one frame by making the frame a singleton
	 */
	private static UIControl _instance;
	
	/**
	 * A list of all the screens that have been made so they can be re opened at any point
	 */
	private final ArrayList<JPanel> _Screens;
	
	/**
	 * The Container that contains the panels
	 */
	private final Container _ContentContainer;
	
	/**
	 * The Main menu screen
	 */
	private MainMenu _MainMenuScreen;
	
	/**
	 * The in game screen
	 */
	private InGame _InGameScreen;
	
	/**
	 * The lobby screen
	 */
	private Lobby _LobbyScreen;
	
	/**
	 * The server screen
	 */
	private Server _ServerScreen;
	
	/**
	 * The connection screen
	 */
	private Connection _ConnectionScreen;
	
	/**
	 * Gets the singleton instance of the UIControl and creates one if it does not exist
	 * @return The instance of the UIControl
	 */
	public static UIControl GetInstance()
	{
		if(_instance == null)
		{
			_instance = new UIControl();
		}
		
		return _instance;
	}
	
	/**
	 * Creates a UIControl
	 * 
	 * Sets up the frame
	 */
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
	
	/**
	 * Hides all other screens and displays the main menu screen
	 */
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

	/**
	 * Hides all the screens that are in the list of screens
	 */
	private void HideAllScreens() {
		_Screens.stream().forEach(s -> s.setVisible(false));
	}

	/**
	 * Hides all other screens and displays the Server screen
	 */
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

	/**
	 * Hides all other screens and displays the lobby screen
	 * @param host If the user opening the lobby is hosting the server
	 */
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

	/**
	 * Hides all other screens and displays the Game screen
	 */
	public void OpenGame() {
		
		HideAllScreens();		
		if(_InGameScreen == null)
		{
			_InGameScreen = new InGame(Controller.GetController().get_GameController().GetLocalPlayer());
			_Screens.add(_InGameScreen);
			_ContentContainer.add(_InGameScreen);
		}
		
		
		this.setSize(1500,1000);
		_InGameScreen.setVisible(true);
	}

	/**
	 * Updates a frame of the in game screen
	 */
	public void UpdateIngameScreen() {
		if (_InGameScreen != null)
			_InGameScreen.Frame();
		
	}
	
	/**
	 * Hides all other screens and displays the connection screen
	 * @param autoConnect Attempts to automatically connect to the server on the local host address
	 */
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

	/**
	 * Appends the lobby chat with the message of players connecting and disconnecting
	 * @param player The player that connected or disconnected
	 * @param connected True if it was a connection false if disconnection
	 */
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
	
	/**
	 * Adds a chat message to the lobby screen
	 * @param message The message to write to the lobby screen
	 */
	public void AddChatMessage(String message)
	{
		_LobbyScreen.AppendChat(message);
	}
	
	/**
	 * Adds a chat message to the lobby screen and takes a player to update the player list
	 * @param p The player to update the player list with
	 * @param message The message to write to the lobby screen
	 */
	public void AddChatMessage(Player p, String message)
	{
		_LobbyScreen.AppendChat(message);
		_LobbyScreen.UpdateList(p);
	}

	/**
	 * Opens a message pane window with the specified parameters 
	 * @param text The message to give the user
	 * @param windowName The name of the window
	 * @param MessageType The type of message it is. Use JOptionPane for the enum
	 */
	public void OpenMessagePane(String text, String windowName, int MessageType ) {
		JOptionPane.showMessageDialog(this, text, windowName, MessageType);
	}
	
	/**
	 * Displays an option pane with the yes no cancel options
	 * @param text The message to give the user
	 * @return the index of the answer. Use JOptionPane for the enum
	 */
	public int OpenOptionsPanel(String text)
	{
		return JOptionPane.showConfirmDialog(this, text);
	}

	/**
	 * Sends a player name update message to the chat and updates it in the player list
	 * @param player
	 */
	public void PlayerNameUpdate(Player player) {
		_LobbyScreen.AppendChat("A player has Changed their name to " + player.getName());
		_LobbyScreen.UpdateList(player);
	}
}
