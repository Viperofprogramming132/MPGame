package com.Viper.Control;

import java.util.ArrayList;

import com.Viper.Sound.SoundController;
import com.Viper.UI.UIControl;

/**
 * The controller is the main connection between all the classes it connects the UI control to the functionality of the program
 * @author Aidan
 *
 */
public class Controller {
	
	/**
	 * Static instance of the controller so that any class can get it to call functionality
	 */
	private static Controller _instance;
	
	/**
	 * The UI controller that is the frame and controls all the panels of the program 
	 */
	private UIControl _UIController;
	
	/**
	 * The index of the map that has been selected set as you enter a server
	 */
	private int _SelectedMap;
	
	/**
	 * The control for the game itself containing the timer and collision management systems
	 */
	private Game _GameController;
	
	/**
	 * Controls reading and playing of sound clips
	 */
	private SoundController _SoundControl;
	
	/**
	 * The Observable list of {@link com.Viper.Control.Player} class
	 * 
	 * Notifies when added to or edited so that it can update the UI system
	 */
	private ArrayList<Player> _Players = new ArrayList<Player>();
	
	/**
	 * The singleton function for the controller object
	 * @return The current Controller object
	 */
	public static Controller GetController()
	{
		if (_instance == null)
		{
			_instance = new Controller();
		}
		
		return _instance;
	}
	
	/**
	 * Constructor for the controller creates the listener for the Observable List of the Players along with the sound system
	 */
	private Controller()
	{	
		_SoundControl = new SoundController();
	}


	/**
	 * Starts the application opening the main menu and starting the background music
	 */
	public void StartApp() {
		_UIController = UIControl.GetInstance();
		
		_UIController.OpenMainMenu();
		_SoundControl.StartBackgroundMusic();
	}
	
	/**
	 * Disposes the UI controller
	 */
	public void ExitProgram()
	{
		_UIController.dispose();
	}

	/**
	 * Enters the lobby screen and sets the host to true if the server is made
	 */
	public void EnterLobby() {
		addPlayer(new Player(1));
		addPlayer(new Player(2));
		_UIController.OpenLobby();
	}
	

	/**
	 * Gets the game controller
	 * @return The Current Game Controller
	 */
	public Game get_GameController() {
		return _GameController;
	}
	
	/**
	 * Starts the game with all the players that are currently in the lobby
	 */
	public void StartGame() {
		if (_GameController == null)
			_GameController = new Game(_Players);
		_UIController.OpenGame();
	}
	
	/**
	 * Adds a player to the observable player list 
	 * @param p The player object to add to the list
	 */
	public void addPlayer(Player p) 
	{
		_Players.add(p);
	}
	
	/**
	 * Gets the Player List
	 * @return The Observable Player List
	 */
	public ArrayList<Player> getPlayers()
	{
		return _Players;
	}

	/**
	 * Gets the selected map index
	 * @return the index of the selected map
	 */
	public int get_SelectedMap() {
		return _SelectedMap;
	}
	
	/**
	 * sets the selected map index
	 * @param Index The index of the map that has been chosen
	 */
	public void set_SelectedMap(int Index)
	{
		_SelectedMap = Index;
	}
	
	/**
	 * Opens a Options pane with yes no options
	 * @param text The question to ask in the pane
	 * @return The index of the selected answer can use JOptionPane.YES_OPTION or JOptionPane.NO_OPTION to check
	 */
	public int OpenJOptionsPane(String text)
	{
		return _UIController.OpenOptionsPanel(text);
	}

	/**
	 * Opens a Custom Message Pane for the user to see
	 * @param text The main text that the message will display
	 * @param title The title of the window
	 * @param Option The type of message it is. This can be accessed from JOptionPane for example JOptionPane.INFORMATION_MESSAGE
	 */
	public void OpenMessagePane(String text, String title, int Option)
	{
		_UIController.OpenMessagePane(text, title, Option);
	}

	/**
	 * Gets the sound controller to play the crash sound
	 */
	public void PlayerCrashSound() {
		_SoundControl.PlayCrash();
		
	}
}
