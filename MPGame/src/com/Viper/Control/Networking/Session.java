package com.Viper.Control.Networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.Messages.LobbyInfoMessage;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.Message;
import com.Viper.Control.Networking.Messages.PlayerInfoMessage;

/**
 * Takes all messages on both UDP and TCP and broadcasts them out to all the other users
 * TCP is used for the majority of traffic
 * UDP is used for the game positions
 * @author Aidan
 *
 */
public class Session implements Runnable{
    /**
     * Socket towards the connected client.
     */
    private final Socket _ClientSocket;
    /**
     * The OutputStream that is provided by the socket.
     */
    private volatile ObjectOutputStream _ObjOut;
    /**
     * The InputStream that is provided by the socket.
     */
    private volatile ObjectInputStream _ObjIn;
    
    /**
     * The output stream used for the UDP connection to serialise the message objects
     */
    private volatile ObjectOutputStream _UDPObjOut;
    
    /**
     * The input stream used for the UDP connection to deserialise the message objects
     */
    private volatile ObjectInputStream _UDPObjIn;
    
    /**
     * The output stream used to send the byte array down the UDP socket
     */
    private volatile ByteArrayOutputStream _ByteOut;
    
    /**
     * The input stream used by UDP to get the data from the client to deserialise
     */
    private volatile ByteArrayInputStream _ByteIn;
    
    /**
     * The UDP socket that is bound to the client
     */
    private DatagramSocket _UDPSocket;
    
    /**
     * If the client has said goodbye to the server (aka disconnected)
     */
    private boolean _GoodBye = false;
    
    /**
     * if the server is shutting down
     */
    private boolean _ShuttingDown = false;
    
    /**
     * The count of errors thrown if it goes above 5 the user is disconnected
     */
    private int _ExceptionCounter = 0;
    
    /**
     * The link back to the lobby to broadcast to other clients
     */
	private GameServer _Lobby;
	
	/**
	 * The player that this session is running for
	 */
	private Player _Player;
	
	/**
	 * The IP address of the client that is connected to this session
	 */
	private InetAddress _IPAddress;
	
	/**
	 * The port that UDP will use to receive and send data down
	 */
	private int _Port;
	
	/**
	 * The UDP Thread that listens for updates of the vehicles position
	 */
	private Thread _UDPListener;
    
	/**
	 * Creates a instance of Session and creates a new player and port for them to use
	 * @param ClientSocket The socket that this session will be using
	 * @param lobby The server that this session is hosted on
	 */
    public Session(Socket ClientSocket, GameServer lobby)
    {
    	_ClientSocket = ClientSocket;
    	_Lobby = lobby;
    	_IPAddress = _ClientSocket.getInetAddress();
    	_Player = new Player(_Lobby.getSessionCount() + 1, true);
    	_Port = 8888 + (_Player.getID() * 2);
    }
    
    /**
     * Attempts to open the input and output stream on the TCP socket and creates the UDP streams
     * @return true if opened successfully otherwise false
     */
	public boolean OpenStreams() {
		boolean success = true;
		try {
			_ObjOut = new ObjectOutputStream(_ClientSocket.getOutputStream());
			_ByteOut = new ByteArrayOutputStream();
			_UDPObjOut = new ObjectOutputStream(_ByteOut);
			_ObjIn = new ObjectInputStream(_ClientSocket.getInputStream());
			_Player.setName("Player " + _Player.getID());
			_UDPSocket = new DatagramSocket(_Port - 1, _ClientSocket.getInetAddress());
		} catch (Exception e) {
			success = false;
		}
		
		return success;
	}

	/**
	 * The Main listening thread that reads in all the TCP traffic
	 */
	@Override
	public void run() {
		
		if(_ObjIn == null || _ObjOut == null)
			return;
		
		System.out.println("Session Started");
		Message msg;
		
		while (!_ClientSocket.isClosed() && !_ShuttingDown && _ExceptionCounter < 5)
		{
			try
			{
				msg = (Message) _ObjIn.readObject();
				
				_ExceptionCounter = 0;
				
				if (msg.getType() == MESSAGETYPE.CONNECTED)
				{
					System.out.println("Player Connected");
					
					SendLobbyInfo();
					continue;
				}
                
                //If player dropped message arrives then remove the the session from the launch lobby
                if (msg.getType() == MESSAGETYPE.DISCONNECTED) {
                	_GoodBye = true;
                	
                    System.out.println("Player dropped message received from the client.");
                    //Check if the player was in a lobby
                    if (_Lobby.ContainsClient(this)) {
                        _Lobby.DisconnectClient(this);
                    }
                    BroadcastMessage(msg);
                    continue;
                }
                
                if (msg.getType() == MESSAGETYPE.CHATMESSAGE)
                {
                	BroadcastMessage(msg);
                	continue;
                }
                
                if(msg.getType() == MESSAGETYPE.PLAYERINFO)
                {
                	PlayerInfoMessage plInfo = (PlayerInfoMessage) msg;
                	_Player.setReady(plInfo.is_Ready());
                	_Player.setName(plInfo.get_Name());
                	_Player.setSpriteIndex(plInfo.get_SelectedVehicleIndex());
                	
                	BroadcastMessage(plInfo);
                	continue;
                }
                if(msg.getType() == MESSAGETYPE.GAMESTART)
                {
                	_Lobby.SendGameStartToClients();
                	continue;
                }
			}
			catch (Exception e)
			{
				if (!_ShuttingDown && !_GoodBye && !_ClientSocket.isClosed())
				{
					if (_Lobby.ContainsClient(this))
						_Lobby.DisconnectClient(this);
					_ExceptionCounter++;
				}
			}
			
			
		}
		
        if (_ClientSocket != null) {
            if (!_ClientSocket.isClosed()) {
                System.out.println("Trying to close client socket.");
                try {
                    _ClientSocket.close();
                    System.out.println("Client socket is closed.");
                } catch (IOException e) {
                	System.out.println("Could not close the client socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
		
	}
	
	/**
	 * The UDP reading thread reads in bytes
	 * Deserialises and broadcasts them out to the other users
	 */
	private Runnable ReceiveStatus = () -> {
		while (!_GoodBye && !_ShuttingDown && _ExceptionCounter < 5) {
			byte[] buffer = new byte[1000];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			
			try {
				_UDPSocket.receive(p);
				_ByteIn = new ByteArrayInputStream(p.getData());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Message msg = null;
			
			try {
				_UDPObjIn = new ObjectInputStream(_ByteIn);
				msg = (Message) _UDPObjIn.readObject();
				_UDPObjIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (msg != null)
				BroadcastUDPMessage(msg);

		}
	};
	
	/**
	 * Sends a player info message out to all the current players
	 */
	public void SendPlayer()
	{
		PlayerInfoMessage msg = new PlayerInfoMessage(MESSAGETYPE.PLAYERINFO, _Player.getID());
		
		msg.set_Ready(_Player.isReady());
		msg.set_SelectedVehicleIndex(_Player.getSpriteIndex());
		msg.set_Name(_Player.getName());
		
		BroadcastMessage(msg);
	}

	/**
	 * Broadcasts a TCP message out to all the other sessions connected to the server
	 * @param msg The message to be sent
	 */
	private void BroadcastMessage(Message msg) {
		_Lobby.BroadcastMessage(this, msg);
	}
	
	/**
	 * Broadcasts a UDP message out to all the other sessions connected to the server
	 * @param msg The message to be sent
	 */
	private void BroadcastUDPMessage(Message msg) {
		_Lobby.UDPBroadcastMessage(this, msg);
	}

	/**
	 * Sends the information about the current lobby to the client. sends:
	 * Current players
	 * Selected map
	 */
	private void SendLobbyInfo() {
		
		LobbyInfoMessage msg = new LobbyInfoMessage(MESSAGETYPE.LOBBYINFO, _Player.getID());
		
		ArrayList<Player> p = _Lobby.getPlayers();
		p.remove(_Player);
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		for (Player player : p)
		{
			IDs.add(player.getID());
		}
		
		msg.setMapIndex(Controller.GetController().get_SelectedMap());
		msg.set_CurrentPlayers(IDs);
		
		SendMessage(msg);
	}

	/**
	 * Sends a TCP message to the client
	 * @param msg The message to be sent
	 * @return True if the message was sent otherwise false
	 */
	public synchronized boolean SendMessage(Message msg) {
		
		if(_GoodBye)
			return false;
		
		if (_ClientSocket.isClosed())
			return false;
		
		if(_ShuttingDown)
			return false;
		
		if(_ExceptionCounter > 5)
			return false;
		
		if(msg.getType() == MESSAGETYPE.GAMESTART)
		{
			_UDPListener = new Thread(ReceiveStatus);
			_UDPListener.start();
		}
		
		boolean result = true;
		
		try {
			synchronized (this) {
				_ObjOut.writeObject(msg);
				_ObjOut.flush();
				_ExceptionCounter = 0;
			}
		} catch (Exception e)
		{
			_ExceptionCounter++;
			System.out.println("Send Message Error Occured: " + e);
			result = false;
		}
		
		return result;
	}

	/**
	 * Tells the session that the server is shutting down
	 */
	public void ServerShutDownNotification() {
		_ShuttingDown = true;		
	}
	
	/**
	 * Gets the socket that this session uses
	 * @return
	 */
	public Socket getSocket()
	{
		return _ClientSocket;
	}

	/**
	 * Gets the player that this session is running for 
	 * @return
	 */
	public Player getPlayer() {
		return _Player;
	}

	/**
	 * Sends a UDP message to the client
	 * @param msg The message to be sent
	 * @return True if the message is sent otherwise false
	 */
	public synchronized boolean SendUDPMessage(Message msg) {
		
		if(_GoodBye)
			return false;
		
		if (_ClientSocket.isClosed())
			return false;
		
		if(_ShuttingDown)
			return false;
		
		if(_ExceptionCounter > 5)
			return false;
		
		boolean result = true;
		
		//Reset the streams
		try {
			_ByteOut = new ByteArrayOutputStream();
			_UDPObjOut = new ObjectOutputStream(_ByteOut);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//serialise to an object
		try {
			_UDPObjOut.writeObject(msg);
			_UDPObjOut.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Write the byte array to a buffer
		byte[] buffer = _ByteOut.toByteArray();
		
		//Send the data
		try {
			synchronized (this) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, _IPAddress, _Port);
			    _UDPSocket.send(packet);
			    _ExceptionCounter = 0;
			}
		} catch (Exception e)
		{
			_ExceptionCounter++;
			System.out.println("Send Message Error Occured: " + e + " from client");
			result = false;
		}
		
		_ByteOut.reset();
		
		return result;
	}

}
