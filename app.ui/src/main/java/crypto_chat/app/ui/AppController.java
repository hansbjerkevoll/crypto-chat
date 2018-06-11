package crypto_chat.app.ui;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import tcp_server.TCPServer;

public class AppController {
	
	@FXML Button joinServerButton, hostChatButton;
	
	public static final int DEFAULT_PORT = 64672;
	
	public void initialize(){
		
		hostChatButton.setOnAction(ae -> {
			new Thread(() -> { 
				TCPServer server = new TCPServer(DEFAULT_PORT);
				server.start();
			}).start();
					
		});
		
		joinServerButton.setOnAction(ae -> {
			try {
				Socket socket = new Socket("localhost", DEFAULT_PORT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

}
