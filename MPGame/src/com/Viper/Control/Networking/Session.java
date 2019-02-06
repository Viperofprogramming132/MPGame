package com.Viper.Control.Networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;
import com.Viper.Control.Networking.Messages.LobbyInfoMessage;
import com.Viper.Control.Networking.Messages.MESSAGETYPE;
import com.Viper.Control.Networking.Messages.Message;
import com.Viper.Control.Networking.Messages.PlayerInfoMessage;

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
    
    private boolean _GoodBye = false;
    
    private boolean _ShuttingDown = false;
    
    private int _ExceptionCounter = 0;
    
	private GameServer _Lobby;
	
	private Player _Player;
    
    public Session(Socket ClientSocket, GameServer lobby)
    {
    	_ClientSocket = ClientSocket;
    	_Lobby = lobby;
    	_Player = new Player(_Lobby.getSessionCount() + 2, true);
    }
    
	public boolean OpenStreams() {
		boolean success = true;
		try {
			_ObjOut = new ObjectOutputStream(_ClientSocket.getOutputStream());
			_ObjIn = new ObjectInputStream(_ClientSocket.getInputStream());
		} catch (Exception e) {
			success = false;
		}
		
		return success;
	}

	@Override
	public void run() {
		
		if(_ObjIn == null || _ObjOut == null)
			return;
		
		System.out.println("Session Started");
		Message msg;
		
		while (!_ClientSocket.isClosed() && !_ShuttingDown && _ExceptionCounter < 5)
		{
			try {
				Thread.sleep(2);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
				
                if (msg.getType() == MESSAGETYPE.INGAMEPOSUPDATE) {
                    if (_Lobby != null) {
                    	_Lobby.BroadcastMessage(this, msg);
                    	//System.out.println("Update Recived");
                    }
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
                    _Lobby.BroadcastMessage(this, msg);
                    continue;
                }
                
                if(msg.getType() == MESSAGETYPE.READY)
                {
                	_Player.setReady(!_Player.isReady());
                }
			}
			catch (Exception e)
			{
				if (!_ShuttingDown && !_GoodBye && !_ClientSocket.isClosed())
				{
					if (_Lobby.ContainsClient(this))
						_Lobby.DisconnectClient(this);
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
	
	public void SendPlayer()
	{
		PlayerInfoMessage msg = new PlayerInfoMessage(MESSAGETYPE.PLAYERINFO, _Player.getID());
		
		msg.set_Ready(_Player.isReady());
		msg.set_SelectedVehicleIndex(_Player.getSpriteIndex());
		
		BroadcastMessage(msg);
	}

	private void BroadcastMessage(Message msg) {
		_Lobby.BroadcastMessage(this, msg);
	}

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

	public synchronized boolean SendMessage(Message msg) {
		
		if(_GoodBye)
			return false;
		
		if (_ClientSocket.isClosed())
			return false;
		
		if(_ShuttingDown)
			return false;
		
		if(_ExceptionCounter > 5)
			return false;
		
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

	public void ServerShutDownNotification() {
		_ShuttingDown = true;		
	}
	
	public Socket getSocket()
	{
		return _ClientSocket;
	}

	public Player getPlayer() {
		return _Player;
	}

	public void setPlayer(Player _Player) {
		this._Player = _Player;
	}

}
