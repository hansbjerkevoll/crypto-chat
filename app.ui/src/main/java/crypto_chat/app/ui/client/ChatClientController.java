package crypto_chat.app.ui.client;

public class ChatClientController {

	ClientSocketHandler socketHandler;
	
	public ChatClientController(ClientSocketHandler socketHandler) {
		this.socketHandler = socketHandler;
		this.socketHandler.setController(this);
	}
	
}

