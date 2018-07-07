package crypto_chat.app.core.json_models.json_msg;

import crypto_chat.app.core.json_models.JsonPackage;
import crypto_chat.app.core.json_models.MessageType;

/**
 * Indicate to the other connection that we are closing the connection in an orderly fashion.
 * 
 * Can be sent both from server to client, and client to server.
 */
public class ConnectionClosed extends JsonPackage {
	public ConnectionClosed() {
		messageType = MessageType.CLOSED;
	}
}
