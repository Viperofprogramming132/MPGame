package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

import com.Viper.Control.Networking.Messages.MESSAGETYPE;

public class Message implements Serializable{
	
	//Required for serialisation
	private static final long serialVersionUID = 1L;
	
	//The type of message that is to be sent
	private MESSAGETYPE _MessageType;
	
	//Who sent the message
	private int _PlayerID;
	
	public Message(MESSAGETYPE type, int playerID)
	{
		_MessageType = type;
		_PlayerID = playerID; 
	}
	
	public Message(MESSAGETYPE type)
	{
		_MessageType = type;
	}
	
	public MESSAGETYPE getType()
	{
		return _MessageType;
	}

	public int get_PlayerID() {
		return _PlayerID;
	}	

}
