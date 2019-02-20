package com.Viper.Control.Networking.Messages;

/**
 * An extension of message that contains a string for the message that was sent by the player
 * @author Aidan
 *
 */
public class ChatMessage extends Message{

	/**
	 * The serial ID so the message can be identified either end
	 */
	private static final long serialVersionUID = 8531752199021412254L;
	
	/**
	 * The chat message
	 */
	private String _Message;
	
	/**
	 * Creates a ChatMessage
	 * @param type The Message type. Should be MESSAGETYPE.CHATMESSAGE
	 * @param playerID The player ID who sent the message
	 * @param message The message itself
	 */
	public ChatMessage(MESSAGETYPE type, int playerID, String message) {
		super(type, playerID);
		
		set_Message(message);
	}
	
	/**
	 * @return The chat message
	 */
	public String get_Message() {
		return _Message;
	}
	
	/**
	 * Sets the chat message
	 * @param _Message The chat message to be set
	 */
	public void set_Message(String _Message) {
		this._Message = _Message;
	}

}
