package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;

public abstract class ChatMessage extends JsonPackage {
	
	protected String sender;
	protected long timestamp;

	public String getSender() {
		return this.sender;
	}
	
	public Long getTimeStamp() {
		return this.timestamp;
	}

}
