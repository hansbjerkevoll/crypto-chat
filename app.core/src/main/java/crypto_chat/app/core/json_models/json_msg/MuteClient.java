package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class MuteClient extends JsonPackage{
	
	private boolean isMuted;
	
	public MuteClient(boolean isMuted) {
		messageType = MessageType.MUTED;
		this.isMuted = isMuted;
	}
	
	public boolean getIsMuted() {
		return this.isMuted;
	}
	
}
