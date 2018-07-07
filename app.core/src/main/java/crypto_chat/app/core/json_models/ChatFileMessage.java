package crypto_chat.app.core.json_models;

import java.io.File;

public class ChatFileMessage extends ChatMessage {
	
	private File file;

	public ChatFileMessage(String senderName, File file, Long timeStamp) {
		messageType = MessageType.FILE_MESSAGE;
		this.senderName = senderName;
		this.file = file;
		this.timeStamp = timeStamp;
	}

	public File getFile() {
		return this.file;
	}

}
