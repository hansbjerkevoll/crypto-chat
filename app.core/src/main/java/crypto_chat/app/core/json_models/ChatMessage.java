package crypto_chat.app.core.json_models;

public abstract class ChatMessage extends JsonPackage {
	
	protected String senderName;
	protected long timeStamp;

	public String getSenderName() {
		return this.senderName;
	}
	
	public Long getTimeStamp() {
		return this.timeStamp;
	}

}
