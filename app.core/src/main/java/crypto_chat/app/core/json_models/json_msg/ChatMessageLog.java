package crypto_chat.app.core.json_models.json_msg;

import java.util.ArrayList;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class ChatMessageLog extends JsonPackage {
	
	ArrayList<ChatMessage> chatMessageLog;

	public ChatMessageLog(ArrayList<ChatMessage> chatMessageLog) {
		messageType = MessageType.MESSAGE_LOG;
		this.chatMessageLog = chatMessageLog;
	}
	
	public ArrayList<ChatMessage> getChatMessageLog(){
		return this.chatMessageLog;
	}
	
}
