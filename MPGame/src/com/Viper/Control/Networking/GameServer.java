package com.Viper.Control.Networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.Viper.Control.Networking.Messages.Message;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * The Game server controls all the sessions of the users
 * Each user has its own Session which runs on its own thread
 * @author Aidan
 *
 */
public class GameServer {

	/**
	 * The list of sockets the clients are connected to
	 */
	private final ArrayList<Socket> _ClientSockets = new ArrayList<>();
	
	/**
	 * The list of sessions used to send the information to the clients
	 */
	private final ArrayList<Session> _ClientSessions = new ArrayList<>();
	
	/**
	 * The list of client threads
	 */
	private ExecutorService _ClientThreads;	
	
    /**
     * List of the client sockets with event when they are added
     */
    private final ObservableList<Socket> _ObservablePreGameLobby = FXCollections.observableArrayList(_ClientSockets);
	
    /**
     * If the server will accept new connections from clients
     */
	private boolean _AcceptIncommingConnections = false;
	
	/**
	 * If the server is running should only be false when shutdown is called
	 */
	private boolean _IsRunning = false;
	
	/**
	 * The TCP server socket that will accept the client connections
	 */
	private ServerSocket _ServerSocket;
	
	/**
	 * The Main thread for the server that will accept the clients that are attempting to connect to the server
	 */
	private Thread _AccepterThread;
	
	/**
	 * Accepts client connections while the server is running and accepting connections are true
	 */
	private final Runnable AcceptClientConnections = () ->
    {
        while (_AcceptIncommingConnections && _IsRunning) {
            try {
                Socket aClient = _ServerSocket.accept();
                System.out.println("Client connection accepted: " + aClient.getInetAddress().toString());
                _ObservablePreGameLobby.add(aClient);

                //Create a new Session. Check if the session can open the in/out streams for the socket.
                //If yes, then launch the Session's run on a separate thread.
                Session session = new Session(aClient, this);
                if (session.OpenStreams()) {
                    _ClientSessions.add(session);
                    _ClientThreads.submit(session);
                    session.SendPlayer();
                } else {
                	System.out.println("Could not get the in or out stream for the connection. Connection is closed");
                    aClient.close();
                    _ObservablePreGameLobby.remove(aClient);
                }

            } catch (Exception e) {
                if (_AcceptIncommingConnections && _IsRunning) {
                	System.out.println("ERROR during accepting a client connection: " + e.getMessage());
                }
            }

            if ((!_AcceptIncommingConnections) || (!_IsRunning)) {
            	System.out.println("Main Listening thread with server socket ends now.");
            }
        }

    };
    
    /**
     * Creates a game server
     * adds event to the socket list for adding to print when a user connects
     */
    public GameServer() {
        //Add listener and event handler for the Observable Lobby changes
        _ObservablePreGameLobby.addListener(this::HandleLobbyChange);
    }
    
    
    /**
     * When a user connects this is handles what happens
     * Should not be called should be added as a listener to an observable list
     * @param change The list since the change
     */
    private void HandleLobbyChange(ListChangeListener.Change<? extends Socket> change) {
    	while(change.next())
    	{
    		if (change.wasAdded()) {
            	System.out.println("A new player has added to the lobby, " + change.getAddedSubList().get(0).getInetAddress() + ".");
            	
        	}
    	}
    }
    
    /**
     * Starts the server on the specified port number
     * @param portNumber The number to open the server on
     */
    public void StartServer(int portNumber) {
        if (!_IsRunning) {
        	System.out.println("Launching server...");

            //Open the new socket.
            if (CreateServerSocket(portNumber)) {
            	System.out.println("Server socket created on port " + Integer.toString(portNumber) + ".");
                CreateNewClientThreadPool();
                _AcceptIncommingConnections = true;

                _AccepterThread = new Thread(AcceptClientConnections);
                _AccepterThread.start();

                _IsRunning = true;
            } else {
                _IsRunning = false;
                System.out.println("ERROR. Could not open the Server Socket. Could not start the server.");
            }

        } else {
            _IsRunning = false;
            System.out.println("ERROR. Cannot launch server. Server is already running.");
        }
    }

    /**
     * Creates a new thread pool for the clients
     */
	private void CreateNewClientThreadPool() {
		_ClientThreads = Executors.newCachedThreadPool();
	}

	/**
	 * Creates a new server socket on the specified port
	 * @param port The port in which to create the server socket
	 * @return True if successful otherwise false
	 */
    private boolean CreateServerSocket(int port) {
        boolean result = true;
        try {
            _ServerSocket = new ServerSocket(port);
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
	
	/**
	 * Attempts to stop the server and send a server down message to all the clients that are currently connected
	 */
    public void StopServer() {
        if (_IsRunning) {
        	System.out.println("Server is shutting down...");
        	
            SendServerDownMessageToAllClients();
            
            _IsRunning = false;
            _AcceptIncommingConnections = false;


            if (TryCloseServerSocket()) {
            	System.out.println("Server socket is closed now.");
            } else {
            	System.out.println("Could not close the server socket.");
            }

            _ClientSessions.clear();
            _ObservablePreGameLobby.clear();
            _IsRunning = false;
            System.out.println("Server has stopped.");

        } else {
        	System.out.println("No need to shutdown. Server is not running.");
        }
    }
    
    /**
     * Sends a message of servershutdown to all clients that are currently connected to the server
     */
    private void SendServerDownMessageToAllClients() {
        Message msg = new Message(MESSAGETYPE.SERVERSHUTDOWN);
        if (_ObservablePreGameLobby != null) {
            for (Session _ClientSession : _ClientSessions) {

                if (_ClientSession.SendMessage(msg)) {
                	System.out.println("The server down notification sent to client. ");
                } else {
                	System.out.println("Could not send server down notification to a client.");
                }
                _ClientSession.ServerShutDownNotification();
            }
        }
    }
    
    /**
     * Sends a game start message to all clients that are connected at the current time
     * @return true if the game start message was sent. If the users were not all ready returns false
     */
    public boolean SendGameStartToClients()
    {
    	for (int i = 0; i < _ClientSessions.size(); i++)
    	{
    		if (!_ClientSessions.get(i).getPlayer().isReady())
    		{
    			return false;
    			
    		}
    	}
    	
        Message msg = new Message(MESSAGETYPE.GAMESTART);
        if (_ObservablePreGameLobby != null) {
        	BroadcastMessage(null, msg);
        }
        
        return true;
    }
    
    
    /**
     * Tries close the servers socket
     * @return true if the socket was closed otherwise false
     */
    private boolean TryCloseServerSocket() {
        boolean result = true;

        if (_ServerSocket != null) {
            if (!_ServerSocket.isClosed()) {
                try {
                    _ServerSocket.close();
                } catch (IOException e) {
                    result = false;
                }
            }
        }
        return result;
    }
    
    /**
     * Sends a TCP message to all connected clients apart from the requesting session
     * @param requestingSession The session that requests the broadcast to stop it receiving the message it sent
     * @param msg The message that is to be sent
     */
    public void BroadcastMessage(Session requestingSession, Message msg)
    {
    	for (Session _Session : _ClientSessions)
    	{
    		if(_Session != requestingSession)
    		{
    			if(_Session.SendMessage(msg))
    			{
    				System.out.println("Broadcast Sent");
    			}
    			else
    			{
    				System.out.println("Broadcast failed to Send");
    			}
    		}
    	}
    }
    
    /**
     * Sends a UDP message to all connected clients
     * @param requestingSession The session that requests the broadcast to stop it receiving the message it sent
     * @param msg The message that is to be sent
     */
    public void UDPBroadcastMessage(Session requestingSession, Message msg)
    {
    	for (Session _Session : _ClientSessions)
    	{
    		if(_Session != requestingSession)
    		{
    			if(_Session.SendUDPMessage(msg))
    			{
    				System.out.println("Broadcast Sent");
    			}
    			else
    			{
    				System.out.println("Broadcast failed to Send");
    			}
    		}
    	}
    }
    
    /**
     * Disconnects the specified client from the server and removes the session and socket
     * @param toDisconnect The session to disconnect from the server
     */
    public void DisconnectClient(Session toDisconnect)
    {
    	_ClientSessions.remove(toDisconnect);
    	_ObservablePreGameLobby.remove(toDisconnect.getSocket());
    }
    
    /**
     * Checks if the server contain the specified client
     * @param toCheck The client to check if it exists on the server
     * @return true if the client exists otherwise false
     */
    public boolean ContainsClient(Session toCheck)
    {
    	return _ClientSessions.contains(toCheck);
    }
    
    /**
     * Gets the number of clients that are connected to the server
     * @return The number of clients that are connected
     */
    public int getSessionCount()
    {
    	return _ClientSessions.size();
    }
    
    /**
     * Gets the list of players from all the sessions
     * @return The list of all players connected to the server
     */
    public ArrayList<Player> getPlayers()
    {
    	ArrayList<Player> players = new ArrayList<>();
    	for (Session _Session : _ClientSessions)
    	{
    		players.add(_Session.getPlayer());
    	}
    	
    	return players;
    }
}
