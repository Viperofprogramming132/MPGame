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



public class GameServer {

	private final ArrayList<Socket> _ClientSockets = new ArrayList<>();
	private final ArrayList<Session> _ClientSessions = new ArrayList<>();
	private ExecutorService _ClientThreads;	
	
    /**
     * Adds observability to the _PreGameLobby Map. This allows separate threads writing this data collection
     * and notify the main thread about changes (e.g. new player is waiting in the lobby).
     */
    private final ObservableList<Socket> _ObservablePreGameLobby = FXCollections.observableArrayList(_ClientSockets);
	
	private boolean _AcceptIncommingConnections = false;
	private boolean _IsRunning = false;
	
	private ServerSocket _ServerSocket;
	private Thread _AccepterThread;
	
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
                    //Could not open the in/out streams for the socket. Close the socket and remove from the list.
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
    
    public GameServer() {
        //Add listener and event handler for the Observable Lobby changes
        _ObservablePreGameLobby.addListener(this::HandleLobbyChange);
    }
    
    private void HandleLobbyChange(ListChangeListener.Change<? extends Socket> change) {
    	while(change.next())
    	{
    		if (change.wasAdded()) {
            	System.out.println("A new player has added to the lobby, " + change.getAddedSubList().get(0).getInetAddress() + ".");
            	
        	}
    	}
        
        

    }
    
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

	private void CreateNewClientThreadPool() {
		_ClientThreads = Executors.newCachedThreadPool();
	}

    private boolean CreateServerSocket(int port) {
        boolean result = true;
        try {
            _ServerSocket = new ServerSocket(port);
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
	
	
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
    
    public String GetLocalHostName() {
        String address = "";
        try {
            address = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            address = "Cannot detect.";
        }
        return address;
    }
    
    public void DisconnectClient(Session toDisconnect)
    {
    	_ClientSessions.remove(toDisconnect);
    	_ObservablePreGameLobby.remove(toDisconnect.getSocket());
    }
    
    public boolean ContainsClient(Session toCheck)
    {
    	return _ClientSessions.contains(toCheck);
    }
    
    public int getSessionCount()
    {
    	return _ClientSessions.size();
    }
    
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
