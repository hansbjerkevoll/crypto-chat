package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class ClientConnectionResponse extends JsonPackage{
	
	Boolean accepted;
	String servername, response_msg;
	
	public ClientConnectionResponse(boolean accepted, String servername, String response_msg) {
		messageType = MessageType.CONNECTION_RESPONSE;
		this.accepted = accepted;
		this.servername = servername;
		this.response_msg = response_msg;
	}
	
	public Boolean isAccepted() {
		return this.accepted;
	}
	
	public String getServername() {
		return this.servername;
	}
	
	public String getResponseMsg() {
		return this.response_msg;
	}
	
}
