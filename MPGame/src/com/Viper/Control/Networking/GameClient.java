package com.Viper.Control.Networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.Messages.LobbyInfoMessage;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.Message;
import com.Viper.Control.Networking.Messages.PlayerInfoMessage;
import com.Viper.Control.Networking.Messages.VehicleUpdateMessage;
import com.Viper.Model.Vehicle;

public class GameClient {
	
	private volatile Socket _ServerCon;

    /**
     * The OutputStream that is provided by the socket.
     */
    private volatile ObjectOutputStream _ObjOut;
    /**
     * The InputStream that is provided by the socket.
     */
    private volatile ObjectInputStream _ObjIn;
    
    private volatile boolean _ServerDown = false;
    
    private Thread _StartThread;
    
    private volatile boolean _AwaitingResponse = false;
    
    private volatile boolean _ExpectedClose = false;
    
    private volatile boolean _GameStarted = false;
    
    private Message _StartResponse;
    
    Thread _Messenger;
    
    private final Runnable WaitForStart = () ->
    {
    	Message serverResponse;
    	while(!_GameStarted)
    	{
	    	try
	    	{
	    		_AwaitingResponse = true;
	    		_StartResponse = null;
	    		serverResponse = (Message) _ObjIn.readObject();
	    		
	    		if (serverResponse.getType() == MESSAGETYPE.GAMESTART)
	    		{
	    			_StartResponse = serverResponse;
	    			_AwaitingResponse = false;
	    			_GameStarted = true;
	    			Controller.GetController().StartGame();
	    		}
	    		else if (serverResponse.getType() == MESSAGETYPE.SERVERSHUTDOWN)
	    		{
	    			_AwaitingResponse = false;
	    			
	    			_ServerDown = true;
	    			_ServerCon.close();
	    			_ServerCon = null;
	    		}
	    		else if(serverResponse.getType() == MESSAGETYPE.PLAYERINFO)
	    		{
	    			PlayerInfoMessage msg = (PlayerInfoMessage) serverResponse;
	    			Player p = new Player(msg.get_PlayerID(), true);
	    			p.setReady(msg.is_Ready());
	    			p.setSpriteIndex(msg.get_SelectedVehicleIndex());
	    			
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
	    		}
	    		else
	    		{
	    			System.out.println("Unexpected Message from server");
	    		}
	    	}
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    	}
    	}
    };
    
    private volatile VehicleUpdateMessage _LastUpdateSent;
    
    private Vehicle _Vehicle;
    
    private boolean _ListeningForUpdates = false;
    
    private Thread _MainListeningThread;
    
    private Player _LocalPlayer;
    
    private final Runnable MyUpdateListener = () ->
    {
        while (_ListeningForUpdates && !_ServerDown) {
            Message msg = null;
            try {
            	synchronized (this) {
                	if(_AwaitingResponse)
                	{
                		msg = (Message) _ObjIn.readObject();
                		_AwaitingResponse = false;
                	}
				}


            } catch (Exception e) {
                msg = null;
                e.printStackTrace();
            }
            

            if (msg != null) {
                //If opponent's car update message received (this is the most common).
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
                }

                //If server down message received then stop listening and notify GameEngine
                if (msg.getType() == MESSAGETYPE.SERVERSHUTDOWN) {
                    _ServerDown = true;
                    _ListeningForUpdates = false;
                    
                    //TODO QUIT;
                }
            }
        }
    };
    
    private final Runnable SendStatus = () ->
    {
    	while(!_ServerDown)
    	{
	        if (!_ServerDown) {
	            if (_ListeningForUpdates) {
	                if (_LastUpdateSent != null) {
	                    //Sending out the message and checking for error
	                    if (!SendOut(_LastUpdateSent)) {
	                        //If error occurred
	                        _ListeningForUpdates = false;
	                        System.out.println("Error Message not sent");
	                    }
	                    else
	                    {
	                    	//System.out.println("Update Sent");
	                    	_AwaitingResponse = true;
	                    }
	                }
	            }
	        }
	    	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    };
    
    private synchronized boolean SendOut(Message msg)
    {
        boolean result = false;
        try {
            _ObjOut.writeObject(msg);
            result = true;
        } catch (Exception e) {
            System.out.println("Error Sending Message: ");
            e.printStackTrace();
        }
        
        return result;
    }
    
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
    
    private synchronized boolean OpenOut() {
        try {
            _ObjOut = new ObjectOutputStream(_ServerCon.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean ConnectToServer(String serverAddress, int portNumber) {
        boolean result = true;

        if (TryCloseCurrentConnection(true)) {
            try {
                _ServerCon = new Socket(serverAddress, portNumber);
                _ServerCon.setSoTimeout(0);
                System.out.println("Connected to the server successfully.");

                //Getting input and output streams
                if (OpenOut()) {
                    _ObjIn = new ObjectInputStream(_ServerCon.getInputStream());

                    //Sending a hello message
                    if (SendOut(new Message(MESSAGETYPE.CONNECTED))) {
                    	System.out.println("Hello sent to server.");
                        _ServerDown = false;
                        set_ExpectedClose(false);
                        try
                        {
                        	Message msg = (Message) _ObjIn.readObject();
                        	if (msg.getType() == MESSAGETYPE.LOBBYINFO)
                        	{
                        		LobbyInfoMessage mapMsg = (LobbyInfoMessage) msg;
                        		
                        		_LocalPlayer = new Player(mapMsg.get_PlayerID(), false, this);
                        		Controller.GetController().set_SelectedMap(mapMsg.getMapIndex());
                        		
                        		Controller.GetController().addPlayer(_LocalPlayer);
                        		for (int i : mapMsg.get_CurrentPlayers())
                        		{
                        			Player p = new Player(i, true);
                        			Controller.GetController().addPlayer(p);
                        		}
                        	}
                        }
                        catch (Exception e)
                        {
                        	e.printStackTrace();
                        }
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
        
		_StartThread = new Thread(WaitForStart);
		_StartThread.start();

        return result;
    }
    
    public boolean TryCloseCurrentConnection(boolean sayGoodBye) {
        boolean result = true;
        if (_ServerCon != null) {
            if (!_ServerCon.isClosed()) {


                if (sayGoodBye) {
                    if (SendOut(new Message(MESSAGETYPE.DISCONNECTED)))
                    	System.out.println("Goodbye sent to the server.");
                    else
                    	System.out.println("Could not say Goodbye to the server.");
                }

                //If a thread waits for map response, it needs to end.
                if (_AwaitingResponse) {
                	_AwaitingResponse = false;
                    set_ExpectedClose(true);
                    _StartThread.interrupt();
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
            }
        }
        return result;
    }
	
	public void SendVehicleUpdate(VehicleUpdateMessage message) {
		_LastUpdateSent = message;
		if(_Messenger == null)
		{
			_Messenger = new Thread(SendStatus);
			_Messenger.start();
		}
	}

	public void NotifyOfUpdates(Vehicle r)
	{
		_Vehicle = r;
	}
	
	public void StartListening()
	{
		if (_Vehicle != null)
		{
			_ListeningForUpdates = true;
			_MainListeningThread = new Thread(MyUpdateListener);
			_MainListeningThread.start();
		}
	}
	
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
	
	public boolean IsAwaitingResponse()
	{
		return _AwaitingResponse;
	}

	public boolean is_ExpectedClose() {
		return _ExpectedClose;
	}

	public void set_ExpectedClose(boolean _ExpectedClose) {
		this._ExpectedClose = _ExpectedClose;
	}
	
	public void Ready(int SelectedCarIndex)
	{
		Message msg = new Message(MESSAGETYPE.READY, _LocalPlayer.getID());
		
		SendOut(msg);
	}
	
	public Player getLocalPlayer()
	{
		return _LocalPlayer;
	}
}
