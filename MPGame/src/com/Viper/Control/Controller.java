package com.Viper.Control;

import javax.swing.JOptionPane;

import com.Viper.Control.Networking.GameClient;
import com.Viper.Control.Networking.GameServer;
import com.Viper.Sound.SoundController;
import com.Viper.UI.UIControl;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
	 * The client to send and receive packets from the sever with both TCP and UDP
	 */
	private GameClient _Client;
	
	/**
	 * The server of the game that receives packets and broadcasts them out to the other users
	 */
	private GameServer _Server;
	
	/**
	 * This is set to true if the current running application is running the server
	 */
	private boolean _Host = false;
	
	/**
	 * Controls reading and playing of sound clips
	 */
	private SoundController _SoundControl;
	
	/**
	 * The Observable list of {@link com.Viper.Control.Player} class
	 * 
	 * Notifies when added to or edited so that it can update the UI system
	 */
	private ObservableList<Player> _ObPlayers = FXCollections.observableArrayList();
	
	/**
	 * The count of the number of players that the game is currently working for
	 */
	private int _PlayerCount = 0;
	
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
		_ObPlayers.addListener(new ListChangeListener<Player>() {

			@Override
			public void onChanged(Change<? extends Player> e) {
				while(e.next())
				{
					if(e.wasReplaced())
					{
						if (e.getAddedSubList().get(0).isReady())
							_UIController.AddChatMessage(e.getAddedSubList().get(0), e.getAddedSubList().get(0).getName() + " is now ready");
						else
							_UIController.AddChatMessage(e.getAddedSubList().get(0), e.getAddedSubList().get(0).getName() + " is now not ready");
					}
					if(e.wasAdded())
					{
						if(_PlayerCount != _ObPlayers.size())
							_UIController.PlayerConnectedUpdate(e.getAddedSubList().get(0), true);
						else
						{
							System.out.println("Player name update");
						}
					}
				}
			}
		});
		
		_SoundControl = new SoundController();
	}


	/**
	 * Starts the application opening the main menu and starting the background music
	 */
	public void StartApp() {
		_UIController = UIControl.GetInstance();
		
		_UIController.OpenMainMenu();
		//_SoundControl.StartBackgroundMusic();
	}
	
	/**
	 * Disposes the UI controller
	 */
	public void ExitProgram()
	{
		_UIController.dispose();
	}
	
	/**
	 * Opens the server screen to enable the user to host a game
	 */
	public void HostGame()
	{
		_UIController.OpenServer();
	}

	/**
	 * Enters the lobby screen and sets the host to true if the server is made
	 */
	public void EnterLobby() {
		if(_Server != null)
			_Host = true;
		
		_UIController.OpenLobby(_Host);
	}
	
	
	/**
	 * Starts the server on port 8888 if it is not null
	 */
	public void StartServer()
	{
		if(_Server == null)
			_Server = new GameServer();
		
		_Server.StartServer(8888);
	}
	
	/**
	 * Connects the the server at the specified address on port 8888
	 * @param address The IP address for the server
	 * @return true if the connection was a success otherwise false
	 */
	public boolean ConnectToServer(String address) {
		if(_Client == null)
			_Client = new GameClient();
		
		return _Client.ConnectToServer(address, 8888);
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
			_GameController = new Game(_ObPlayers);
		_UIController.OpenGame();
	}
	
	/**
	 * Sends the ready to the server
	 */
	public void SendReady()
	{
		_Client.getLocalPlayer().setReady();
	}
	
	/**
	 * Opens the connection windows
	 * @param autoConnect true to auto connect to 127.0.0.1 otherwise false
	 */
	public void ConnectToGame(boolean autoConnect)
	{
		_UIController.OpenConnection(autoConnect);
	}
	
	/**
	 * Adds a player to the observable player list 
	 * @param p The player object to add to the list
	 */
	public void addPlayer(Player p) 
	{
		_ObPlayers.add(p);
		_PlayerCount++;
	}
	
	/**
	 * Gets the Player List
	 * @return The Observable Player List
	 */
	public ObservableList<Player> getPlayers()
	{
		return _ObPlayers;
	}
	
	/**
	 * Gets the client to send messages
	 * @return
	 */
	public GameClient getClient()
	{
		return _Client;
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
	 * Adds a message to the chat box in the lobby screen
	 * @param message The message to add to the chat box
	 */
	public void addChatMessage(String message)
	{
		_UIController.AddChatMessage(message);
	}
	
	/**
	 * Updates the local players name and sends it to the client to broadcast to the rest of the clients
	 * @param text The player name to update
	 */
	public void UpdatePlayer(String text) {
		_Client.getLocalPlayer().setName(text);
		
		_Client.PlayerUpdate(_Client.getLocalPlayer().getSpriteIndex());		
	}
	
	/**
	 * Tells the client to send the game start message to all clients (does not send if everyone is not ready)
	 */
	public void SendGameStart()
	{
		_Client.StartGame();
	}

	/**
	 * Disconnects from the server and if the server is open closes the server
	 * @param sayGoodbye True if the disconnection should say goodbye to the server so the other clients know that the disconnect has happened otherwise false
	 */
	public void Disconnect(boolean sayGoodbye) {
		//Close the client connection
		if (_Client != null)
		{
			_Client.TryCloseCurrentConnection(sayGoodbye);
			_Client = null;
		}
		
		//Ensure the Client Disconnect has been processed before shutting the server
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Close the server
		if(_Server != null)
		{
			_Server.StopServer();
			_Server = null;
		}
		
		//Close the game controller
		if(_GameController != null)
		{
			_GameController.CloseGame();
			_GameController = null;
			_Host = false;
		}
		
		//Attempt to reopen the Main Menu
		_UIController.OpenMainMenu();
	}

	/**
	 * Shows a message pane that notifies the user the server has closed and attempts to close connections and exit the application
	 */
	public void ServerClosed() {
		_UIController.OpenMessagePane("Server Was Closed. Closing the game", "Server Closed", JOptionPane.WARNING_MESSAGE);
		Disconnect(false);		
		
		System.exit(0);
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
