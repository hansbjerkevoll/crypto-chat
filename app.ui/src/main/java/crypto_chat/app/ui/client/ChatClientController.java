package crypto_chat.app.ui.client;

import crypto_chat.app.ui.server.ClientSocketHandler;

public class ChatClientController {

	ClientSocketHandler socketHandler;
	
	public ChatClientController(ClientSocketHandler socketHandler) {
		this.socketHandler = socketHandler;
		this.socketHandler.setController(this);
	}
	
}

