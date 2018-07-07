package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class ClientConnectionResponse extends JsonPackage{
	
	Boolean accepted;
	String servername;
	
	public ClientConnectionResponse(boolean accepted, String servername) {
		messageType = MessageType.CONNECTION_RESPONSE;
		this.accepted = accepted;
		this.servername = servername;
	}
	
	public Boolean isAccepted() {
		return this.accepted;
	}
	
	public String getServername() {
		return this.servername;
	}
	
}
