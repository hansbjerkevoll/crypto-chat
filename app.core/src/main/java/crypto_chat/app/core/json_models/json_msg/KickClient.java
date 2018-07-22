package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

public class KickClient extends JsonPackage{

	public KickClient() {
		messageType = MessageType.KICKED;
	}
	
}
