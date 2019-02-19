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
    
    private volatile ObjectOutputStream _UDPObjOut;
    
    private volatile ObjectInputStream _UDPObjIn;
    
    private volatile ByteArrayOutputStream _ByteOut;
    
    private volatile ByteArrayInputStream _ByteIn;
    
    private DatagramSocket _UDPSocket;
    
    private boolean _GoodBye = false;
    
    private boolean _ShuttingDown = false;
    
    private int _ExceptionCounter = 0;
    
	private GameServer _Lobby;
	
	private Player _Player;
	
	private InetAddress _IPAddress;
	
	private int _Port;
	
	private Thread _UDPListener;
    
    public Session(Socket ClientSocket, GameServer lobby)
    {
    	_ClientSocket = ClientSocket;
    	_Lobby = lobby;
    	_IPAddress = _ClientSocket.getInetAddress();
    	_Player = new Player(_Lobby.getSessionCount() + 1, true);
    	_Port = 8888 + (_Player.getID() * 2);
    }
    
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
                }
                
                if(msg.getType() == MESSAGETYPE.PLAYERINFO)
                {
                	PlayerInfoMessage plInfo = (PlayerInfoMessage) msg;
                	_Player.setReady(plInfo.is_Ready());
                	_Player.setName(plInfo.get_Name());
                	_Player.setSpriteIndex(plInfo.get_SelectedVehicleIndex());
                	
                	BroadcastMessage(plInfo);
                }
                if(msg.getType() == MESSAGETYPE.GAMESTART)
                {
                	_Lobby.SendGameStartToClients();
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
	
	public void SendPlayer()
	{
		PlayerInfoMessage msg = new PlayerInfoMessage(MESSAGETYPE.PLAYERINFO, _Player.getID());
		
		msg.set_Ready(_Player.isReady());
		msg.set_SelectedVehicleIndex(_Player.getSpriteIndex());
		msg.set_Name(_Player.getName());
		
		BroadcastMessage(msg);
	}

	private void BroadcastMessage(Message msg) {
		_Lobby.BroadcastMessage(this, msg);
	}
	
	private void BroadcastUDPMessage(Message msg) {
		_Lobby.UDPBroadcastMessage(this, msg);
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
