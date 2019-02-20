package com.Viper.Control.Networking.Messages;

import java.io.Serializable;

import com.Viper.Control.Networking.Messages.MESSAGETYPE;

/**
 * The message that can be serialised to be sent between the client and the server easily
 * 
 * Contains all the information that the application would need to identify the user that sent it
 * 
 * Contains what type of message it is to easily determine what to do with the message
 * @author Aidan
 *
 */
public class Message implements Serializable{
	
	/**
	 * The serial ID so the message can be identified either end
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The type of message that is to be sent
	 */
	private MESSAGETYPE _MessageType;
	
	/**
	 * Who sent the message
	 */
	private int _PlayerID;
	
	/**
	 * Creates a Message of specified type from the player of the specified player ID
	 * @param type The type of message that is being sent
	 * @param playerID The ID of the player that sent the message
	 */
	public Message(MESSAGETYPE type, int playerID)
	{
		_MessageType = type;
		_PlayerID = playerID; 
	}
	
	/**
	 * Creates a message of the specified type. Should only be used if it doesnt matter who sent the message
	 * @param type The type of message that is sent
	 */
	public Message(MESSAGETYPE type)
	{
		_MessageType = type;
	}
	
	/**
	 * @return The type of message
	 */
	public MESSAGETYPE getType()
	{
		return _MessageType;
	}

	/**
	 * Gets the Players ID of who sent it
	 * @return The Player ID of who sent the message
	 */
	public int get_PlayerID() {
		return _PlayerID;
	}	

}
