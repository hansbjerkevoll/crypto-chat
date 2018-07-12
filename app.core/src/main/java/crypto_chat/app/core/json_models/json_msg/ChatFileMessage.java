package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.MessageType;
import java.io.File;

public class ChatFileMessage extends ChatMessage {

	private File file_msg;
	
	public ChatFileMessage(String sender, File file_msg, long timestamp) {
		messageType = MessageType.FILE_MESSAGE;
		this.sender = sender;
		this.file_msg = file_msg;
		this.timestamp = timestamp;
	}
	
	public File getFileMessage() {
		return file_msg;
	}
	
}
