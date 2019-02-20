package com.Viper.Control.Networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.Messages.ChatMessage;
import com.Viper.Control.Networking.Messages.LobbyInfoMessage;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.Message;
import com.Viper.Control.Networking.Messages.PlayerInfoMessage;
import com.Viper.Control.Networking.Messages.VehicleUpdateMessage;
import com.Viper.Model.Vehicle;

/**
 * The connection to the server
 * Uses TPC for most traffic
 * Uses UDP for sending the player location to ensure speed
 * 
 * @author Aidan
 *
 */
public class GameClient {
	
	/**
	 * The socket that is the connection to the server
	 */
	private volatile Socket _ServerCon;

    /**
     * The OutputStream that is provided by the socket.
     */
    private volatile ObjectOutputStream _ObjOut;
    /**
     * The InputStream that is provided by the socket.
     */
    private volatile ObjectInputStream _ObjIn;
    
    /**
     * Boolean of whether the server is down when set to true most of the loops stop
     */
    private volatile boolean _ServerDown = false;
    
    /**
     * The thread that deals with all the TCP traffic
     */
    private Thread _TCPThread;
    
    /**
     * Set to true if awaiting a response false if processing one
     */
    private volatile boolean _AwaitingResponse = false;
    
    /**
     * The last TCP message received
     */
    private Message _Response;
    
    /**
     * The error counter if it gets above 5 the client shuts down
     */
    private int _ErrorCount = 0;
    
    /**
     * The IPAddress the server is at
     */
	private InetAddress _IPAddress;
	
	/**
	 * The port as UDP requires 2 ports it is calculated off the player ID
	 */
	private int _Port;
	
	/**
	 * Serialises the objects into _ByteOut for sending down UDP socket
	 */
    private volatile ObjectOutputStream _UDPObjOut;
    
    /**
     * Takes packets received from the UDP socket and converts them back to a object to process
     */
    private volatile ObjectInputStream _UDPObjIn;
    
    /**
     * Used for sending bytes through the UDP socket    
     */
    private volatile ByteArrayOutputStream _ByteOut;

    /**
     * Packets received from the UDP socket are written to this
     */
    private volatile ByteArrayInputStream _ByteIn;
    
    /**
     * The UDP socket connection to the server uses port 8888 + (Player ID * 2) and 8888 + ((Player ID * 2) - 1)
     */
    private DatagramSocket _UDPSocket;
    
    /**
     * The UDP sending thread
     */
    Thread _Messenger;
    
    /**
     * The Runnable that listens for all the UDP packets
     * Should process everything the server gives but speed vital information like player positions
     */
    private final Runnable WaitForStart = () ->
    {
    	
    	Message serverResponse;
    	while(_ErrorCount < 5)
    	{
	    	try
	    	{
	    		
	    		_AwaitingResponse = true;
	    		_Response = null;
	    		//Get the packet
	    		serverResponse = (Message) _ObjIn.readObject();
	    		
	    		//Check what type it is
	    		if (serverResponse.getType() == MESSAGETYPE.GAMESTART)
	    		{
	    			_Response = serverResponse;	    			
	    			_AwaitingResponse = false;
	    			Controller.GetController().StartGame();
	    			_ErrorCount = 0;
	    		}
	    		else if (serverResponse.getType() == MESSAGETYPE.SERVERSHUTDOWN)
	    		{
	    			_AwaitingResponse = false;
	    			
	    			_ServerDown = true;
	    			TryCloseCurrentConnection(false);
	    			
	    			Controller.GetController().ServerClosed();
	    		}
	    		else if(serverResponse.getType() == MESSAGETYPE.PLAYERINFO)
	    		{
	    			PlayerInfoMessage msg = (PlayerInfoMessage) serverResponse;
	    			Player p = new Player(msg.get_PlayerID(), true);
	    			p.setReady(msg.is_Ready());
	    			p.setSpriteIndex(msg.get_SelectedVehicleIndex());
	    			p.setName(msg.get_Name());
	    			
	    			int i = 0;
	    			boolean Contained = false;
	    			ArrayList<Player> players = new ArrayList<>(Controller.GetController().getPlayers());
	    			for (Player player : players)
	    			{
	    				if(player.getID() == p.getID())
	    				{
	    					Controller.GetController().getPlayers().set(i, p);
	    					Contained = true;
	    				}
	    				i++;
	    			}
	    			if(!Contained)
	    				Controller.GetController().addPlayer(p);
	    			_ErrorCount = 0;
	    		}
	    		else if (serverResponse.getType() == MESSAGETYPE.CHATMESSAGE)
	    		{
	    			ChatMessage chatMsg = (ChatMessage) serverResponse;
	    			
	    			Controller.GetController().addChatMessage(chatMsg.get_Message());
	    			_ErrorCount = 0;
	    		}
	    		else
	    		{
	    			System.out.println("Unexpected Message from server");
	    		}
	    	}
	    	catch (Exception e)
	    	{
	    		_ErrorCount++;
	    	}
    	}
    	
    	if (_ErrorCount > 5)
    	{
    		Controller.GetController().Disconnect(true);
    	}
    };
    
    /**
     * A copy of the last {@link com.Viper.Control.Networking.Messages.VehicleUpdateMessage} that was made to be sent to the server for broadcast
     */
    private volatile VehicleUpdateMessage _LastUpdateSent;
    
    /**
     * The vehicle logic of the local vehicle
     */
    private Vehicle _Vehicle;
    
    /**
     * If the client is listening for vehicle updates for remote players
     */
    private boolean _ListeningForUpdates = false;
    
    /**
     * The main UDP listening thread
     */
    private Thread _MainListeningThread;
    
    /**
     * The local player that this client is running for
     */
    private Player _LocalPlayer;
    
    /**
     * The Vehicle update listener using UDP
     */
    private final Runnable MyUpdateListener = () ->
    {
        while (_ListeningForUpdates && !_ServerDown && _ErrorCount < 5) {
        	
        	//Create the buffer
        	byte[] buffer = new byte[1000];
        	
        	//Create the packet of the size of the buffer
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			
			//Receive the packet
			try {
				_UDPSocket.receive(p);
				_ByteIn = new ByteArrayInputStream(p.getData());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Message msg = null;
			
			//Convert the bytes to the object
			try {
				_UDPObjIn = new ObjectInputStream(_ByteIn);
				msg = (Message) _UDPObjIn.readObject();
				_UDPObjIn.close();
				//System.out.println("UDPMessage Recieved Server Side");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			//Check if the message is an ingameupdate and set it to the players vehicle
            if (msg != null) {
                if (msg.getType() == MESSAGETYPE.INGAMEPOSUPDATE) {
                    VehicleUpdateMessage updateMsg = (VehicleUpdateMessage) msg;
                    int i = 0;
        			for (Player player : Controller.GetController().getPlayers())
        			{
        				if(player.getID() == updateMsg.get_PlayerID())
        				{
        					break;
        				}
        				i++;
        			}
        			Controller.GetController().getPlayers().get(i).get_VehicleLogic().VehicleUpdate(updateMsg);
        			_ErrorCount = 0;
                }
            }
        }
        
        if (_ErrorCount > 5)
        {
        	Controller.GetController().Disconnect(true);
        }
    };
    
    /**
     * The Runnable that sends all the UDP updates to the server
     */
    private final Runnable SendStatus = () ->
    {
    	while(!_ServerDown)
    	{
	        if (!_ServerDown && _ErrorCount < 5) {
	            if (_ListeningForUpdates) {
	                if (_LastUpdateSent != null) {
	                	
	                    //Sending out the message and checking for error
	                    if (!SendOutUDP(_LastUpdateSent)) {
	                        //If error occurred
	                        _ListeningForUpdates = false;
	                        System.out.println("Error Message not sent");
	                        _ErrorCount++;
	                    }
	                    else
	                    {
	                    	//System.out.println("Update Sent");
	                    	_AwaitingResponse = true;
	                    	_ErrorCount = 0;
	                    }
	                }
	            }
	        }
	        //Required to stop over spamming messages
	    	try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	if (_ErrorCount > 5)
	    	{
	    		Controller.GetController().Disconnect(true);
	    	}
    	}

    };
    
    /**
     * Sends out a TCP message
     * @param msg The message to send out
     * @return true if sent otherwise false
     */
    private synchronized boolean SendOut(Message msg)
    {
        boolean result = false;
        try {
            _ObjOut.writeObject(msg);
            result = true;
        } catch (Exception e) {
            System.out.println("Error Sending Message: ");
            e.printStackTrace();
            _ErrorCount++;
        }
        
        return result;
    }
    
    /**
     * Sends out a UDP message
     * @param msg The message to send out
     * @return true if sent otherwise false
     */
	public synchronized boolean SendOutUDP(VehicleUpdateMessage msg) {
		
		boolean result = true;
		try {
			_ByteOut = new ByteArrayOutputStream();
			_UDPObjOut = new ObjectOutputStream(_ByteOut);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			_UDPObjOut.writeObject(msg);
			_UDPObjOut.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		byte[] buffer = _ByteOut.toByteArray();
		
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, _IPAddress, _Port - 1);
		    _UDPSocket.send(packet);
		    _ErrorCount = 0;
			
		} catch (Exception e)
		{
			_ErrorCount++;
			System.out.println("Send Message Error Occured: " + e + " from client");
			result = false;
		}
		
		
		return result;
	}
    
	/**
	 * Closes the TCP object out connection
	 * @return true if successful 
	 */
    private synchronized boolean CloseOut()
    {
        if (_ObjOut != null) {
            try {
            	_ObjOut.close();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Opens the TCP object out connection
     * @return true if successful
     */
    private synchronized boolean OpenOut() {
        try {
            _ObjOut = new ObjectOutputStream(_ServerCon.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * Attempts to connect to a server at a the given IPAddress and Port
     * @param serverAddress The address to attempt to connect to
     * @param portNumber The port to attempt the connection on
     * @return true if the connection was successful
     */
    public boolean ConnectToServer(String serverAddress, int portNumber) {
        boolean result = true;

        //Close any connections already open
        if (TryCloseCurrentConnection(true)) {
            try {
            	_ErrorCount = 0;
                _ServerCon = new Socket(serverAddress, portNumber);
                
                //Set the timeout to 50 seconds
                _ServerCon.setSoTimeout(50000);
                System.out.println("Connected to the server successfully.");

                //Getting input and output streams
                if (OpenOut()) {
                    _ObjIn = new ObjectInputStream(_ServerCon.getInputStream());

                    //Sending a hello message
                    if (SendOut(new Message(MESSAGETYPE.CONNECTED))) {
                    	System.out.println("Hello sent to server.");
                        _ServerDown = false;
                        try
                        {
                        	//Getting the lobby info message containing all current lobby information
                        	Message msg = (Message) _ObjIn.readObject();
                        	
                        	_IPAddress = _ServerCon.getInetAddress();
                        	if (msg.getType() == MESSAGETYPE.LOBBYINFO)
                        	{
                        		LobbyInfoMessage mapMsg = (LobbyInfoMessage) msg;
                        		
                        		_LocalPlayer = new Player(mapMsg.get_PlayerID(), false, this);
                        		Controller.GetController().set_SelectedMap(mapMsg.getMapIndex());
                        		_LocalPlayer.setName("Player " + mapMsg.get_PlayerID());
                        		
                        		Controller.GetController().addPlayer(_LocalPlayer);
                        		for (int i : mapMsg.get_CurrentPlayers())
                        		{
                        			Player p = new Player(i, true);
                        			p.setName("Player " + i);
                        			Controller.GetController().addPlayer(p);
                        		}
                        	}
                        }
                        catch (Exception e)
                        {
                        	e.printStackTrace();
                        }
                        //Create the UDP port
                        _Port = portNumber + (_LocalPlayer.getID() * 2);
                        
                        //Create the UDP socket
                        _UDPSocket = new DatagramSocket(_Port, _IPAddress);
                        //Create the UDP streams
            			_ByteOut = new ByteArrayOutputStream();
            			_UDPObjOut = new ObjectOutputStream(_ByteOut);
                    } else {
                        result = false;
                        System.out.println("Error when tried to send Hello message");
                    }
                } else {
                    result = false;
                    System.out.println("Could not open output stream. Trying to close the socket.");
                    _ServerCon.close();
                    _ServerCon = null;
                    System.out.println("Connection closed");
                }

            } catch (UnknownHostException uh) {
            	_ServerCon = null;
                result = false;
                System.out.println("Unknown host. Cannot connect.");
            } catch (ConnectException ce) {
            	_ServerCon = null;
                result = false;
                System.out.println("ERROR. Server refused the connection.");
            } catch (IOException e) {
            	_ServerCon = null;
                result = false;
                e.printStackTrace();
            }

        } else {
            result = false;
        }
        
        //Start the TCP reading thread
		_TCPThread = new Thread(WaitForStart);
		_TCPThread.start();

        return result;
    }
    
    /**
     * Attempts to close the Current connection
     * @param sayGoodBye True if sending the goodbye message to the server otherwise false
     * @return true if closed the server otherwise false
     */
    public boolean TryCloseCurrentConnection(boolean sayGoodBye) {
        boolean result = true;
        
        //Stops the loops
        _ErrorCount = 10;
        if (_ServerCon != null) {
            if (!_ServerCon.isClosed()) {
            	
                if(_Messenger != null && _Messenger.isAlive())
                	_Messenger.interrupt();

                if (sayGoodBye) {
                    if (SendOut(new Message(MESSAGETYPE.DISCONNECTED)))
                    	System.out.println("Goodbye sent to the server.");
                    else
                    	System.out.println("Could not say Goodbye to the server.");
                }

                //If a thread waits for map response, it needs to end.
                if (_AwaitingResponse) {
                	_AwaitingResponse = false;
                    _TCPThread.interrupt();
                }


                if (!CloseOut())
                	System.out.println("Could not close the object output stream.");


                try {
                    _ObjIn.close();
                } catch (Exception e) {
                    result = false;
                }
                try {
                	_ServerCon.close();
                } catch (Exception e) {
                    result = false;
                    System.out.println("Could  not close the current socket.");
                }


                _ServerCon = null;
                

                if (_MainListeningThread != null && _MainListeningThread.isAlive())
                	_MainListeningThread.interrupt();
                if (_TCPThread != null && _TCPThread.isAlive())
                _TCPThread.interrupt();
            }
        }
        return result;
    }
	
    /**
     * Sets the last update message
     * If the send status thread is not running start that
     * @param message The message to send
     */
	public void SendVehicleUpdate(VehicleUpdateMessage message) {
		_LastUpdateSent = message;
		if(_Messenger == null)
		{
			_Messenger = new Thread(SendStatus);
			_Messenger.start();
		}
	}

	/**
	 * Sets the vehicle that is notified of the updates
	 * @param r The vehicle to send updates to
	 */
	public void NotifyOfUpdates(Vehicle r)
	{
		_Vehicle = r;
	}
	
	/**
	 * Starts the listening thread which listens for vehicle updates
	 */
	public void StartListening()
	{
		if (_Vehicle != null)
		{
			_ListeningForUpdates = true;
			_MainListeningThread = new Thread(MyUpdateListener);
			_MainListeningThread.start();
		}
	}
	
	/**
	 * Stops the listing thread which listens for vehicle updates
	 */
	public void StopListening()
	{
		_ListeningForUpdates = false;
		if(_Vehicle != null)
		{
			if(_MainListeningThread.isAlive())
			{
				_MainListeningThread.interrupt();
			}
			_MainListeningThread = null;
		}
	}
	
	/**
	 * Sends an update of the player to the server
	 */
	public void PlayerUpdate(int SelectedCarIndex)
	{
		PlayerInfoMessage msg = new PlayerInfoMessage(MESSAGETYPE.PLAYERINFO, _LocalPlayer.getID());
		
		msg.set_SelectedVehicleIndex(SelectedCarIndex);
		msg.set_Name(_LocalPlayer.getName());
		msg.set_Ready(_LocalPlayer.isReady());
		SendOut(msg);
	}
	
	/**
	 * gets the local player
	 * @return The player that the user of this client controls
	 */
	public Player getLocalPlayer()
	{
		return _LocalPlayer;
	}
	
	/**
	 * Sends a chat message to the sever to be broadcast
	 * @param message
	 */
	public void SendChatMessage(String message)
	{
		message = _LocalPlayer.getName() + ": " + message;
		ChatMessage msg = new ChatMessage(MESSAGETYPE.CHATMESSAGE, _LocalPlayer.getID(), message);
		SendOut(msg);
	}

	/**
	 * Sends out the Game start message
	 */
	public void StartGame() {
		Message msg = new Message(MESSAGETYPE.GAMESTART);
		
		SendOut(msg);
	}
}
