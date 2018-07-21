package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.MessageType;

public class ChatMessageText extends ChatMessage {
	
	private String text_msg;
	
	public ChatMessageText(String sender, String text_msg, long timestamp) {
		messageType = MessageType.TEXT_MESSAGE;
		this.sender = sender;
		this.text_msg = text_msg;
		this.timestamp = timestamp;
	}
	
	public String getTextMessage() {
		return text_msg;
	}

}
