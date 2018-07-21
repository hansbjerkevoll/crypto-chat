package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.MessageType;
import java.io.File;
import java.io.IOException;

public class ChatMessageFile extends ChatMessage {

	private File file;
	private String file_name;
	
	public ChatMessageFile(String sender, File file, long timestamp) {
		messageType = MessageType.FILE_MESSAGE;
		this.sender = sender;
		this.file = file;
		this.file_name = file.getName();
		this.timestamp = timestamp;
	}
	
	public File getFileMessage() throws IOException {
		return file;
	}
	
	public String getFileName() {
		return this.file_name;
	}
	
}
