package crypto_chat.app.core.json_models.json_msg;

import java.util.ArrayList;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class ClientConnectionResponse extends JsonPackage {
	
	Boolean accepted;
	String servername, response_msg;
	ArrayList<String> chatMessageLog = new ArrayList<>();
	
	public ClientConnectionResponse(boolean accepted, String response_msg) {
		messageType = MessageType.CONNECTION_RESPONSE;
		this.accepted = accepted;
		this.response_msg = response_msg;
	}

	public Boolean isAccepted() {
		return this.accepted;
	}
	
	public String getResponseMsg() {
		return this.response_msg;
	}

	public String getServername() {
		return this.servername;
	}
	
	public void setServername(String servername) {
		this.servername = servername;
	}
	
	public ArrayList<String> getChatMessageLog() {
		return chatMessageLog;
	}

	public void setChatMessageLog(ArrayList<String> chatMessageLog) {
		this.chatMessageLog = chatMessageLog;
	}
	
}
