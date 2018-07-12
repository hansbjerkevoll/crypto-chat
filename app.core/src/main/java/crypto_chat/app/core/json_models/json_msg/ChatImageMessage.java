package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.MessageType;
import javafx.scene.image.Image;

public class ChatImageMessage extends ChatMessage {
	
	private Image image_msg;
	
	public ChatImageMessage(String sender, Image image_msg, long timestamp) {
		messageType = MessageType.IMAGE_MESSAGE;
		this.sender = sender;
		this.image_msg = image_msg;
		this.timestamp = timestamp;
	}
	

	public Image getImageMessage() {
		return image_msg;
	}

}
