package crypto_chat.app.core.json_models.json_msg;

import java.io.ByteArrayInputStream;

import crypto_chat.app.core.json_models.MessageType;
import javafx.scene.image.Image;

public class ChatMessageImage extends ChatMessage {
	
	private  byte[] image_bytes;
	
	public ChatMessageImage(String sender, byte[] image, long timestamp) {
		
		messageType = MessageType.IMAGE_MESSAGE;
		this.sender = sender;
		this.image_bytes = image;
		this.timestamp = timestamp;
	}
	

	public Image getImageMessage() {
		return new Image(new ByteArrayInputStream(image_bytes));
	}

}
