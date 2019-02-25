package com.Viper.UI;

import javax.swing.*;

import com.Viper.UI.InGame.InGame;

import java.awt.*;
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
	 * Hides all other screens and displays the lobby screen
	 * @param host If the user opening the lobby is hosting the server
	 */
	public void OpenLobby() {
		
		HideAllScreens();
		if(_LobbyScreen == null)
		{
			_LobbyScreen = new Lobby();
			_Screens.add(_LobbyScreen);
			_ContentContainer.add(_LobbyScreen);
		}
		
		this.setSize(1500,1000);
		_LobbyScreen.setVisible(true);
		
	}

	/**
	 * Hides all other screens and displays the Game screen
	 */
	public void OpenGame() {
		
		HideAllScreens();		
		if(_InGameScreen == null)
		{
			_InGameScreen = new InGame();
			_Screens.add(_InGameScreen);
			_ContentContainer.add(_InGameScreen);
		}
		
		
		this.setSize(1500,1000);
		_InGameScreen.setVisible(true);
		
		_InGameScreen.grabFocus();
	}

	/**
	 * Updates a frame of the in game screen
	 */
	public void UpdateIngameScreen() {
		if (_InGameScreen != null)
			_InGameScreen.Frame();
		
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

}
