package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class ClientConnectionRequest extends JsonPackage{
	
	private String clientName;
	private String hashedPassword;
	
	public ClientConnectionRequest(String clientName, String hashedPassword) {
		messageType = MessageType.CONNECTION_REQUEST;
		this.clientName = clientName;
		this.hashedPassword = hashedPassword;
	}
	
	public String getClientName() {
		return this.clientName;
	}
	
	public String getHashedPassword() {
		return this.hashedPassword;
	}
}
