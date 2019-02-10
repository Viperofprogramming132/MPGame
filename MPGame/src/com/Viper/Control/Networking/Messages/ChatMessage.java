package com.Viper.Control.Networking.Messages;

public class ChatMessage extends Message{

	private String _Message;
	public ChatMessage(MESSAGETYPE type, int playerID, String message) {
		super(type, playerID);
		
		set_Message(message);
	}
	
	public String get_Message() {
		return _Message;
	}
	public void set_Message(String _Message) {
		this._Message = _Message;
	}

}
