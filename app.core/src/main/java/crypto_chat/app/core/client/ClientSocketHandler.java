package crypto_chat.app.core.client;

import java.net.Socket;

import crypto_chat.app.ui.client.ChatClientController;

public class ClientSocketHandler implements Runnable{
	
	private Socket serverSocket;
	private ChatClientController controller;
	
	public ClientSocketHandler(Socket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void setController(ChatClientController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
