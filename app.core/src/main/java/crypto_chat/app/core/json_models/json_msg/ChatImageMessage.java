package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.MessageType;
import javafx.scene.image.Image;

public class ChatImageMessage extends ChatMessage {
	
	private Image image;

	public ChatImageMessage(String senderName, Image image, Long timeStamp) {
		messageType = MessageType.IMAGE_MESSAGE;
		this.senderName = senderName;
		this.image = image;
		this.timeStamp = timeStamp;
	}

	public Image getImage() {
		return this.image;
	}

}
