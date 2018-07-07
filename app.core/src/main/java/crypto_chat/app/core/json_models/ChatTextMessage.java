package crypto_chat.app.core.json_models;

public class ChatTextMessage extends ChatMessage {
	
	private String message;

	public ChatTextMessage(String senderName, String message, Long timeStamp) {
		messageType = MessageType.TEXT_MESSAGE;
		this.senderName = senderName;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return this.message;
	}
	
}
