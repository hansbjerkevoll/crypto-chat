package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ChatHostController {
	
	@FXML Label ip, name, port, password;
	
	Stage myStage;
	
	ServerSocket serverSocket;
	String serverName;
	String serverPassword;
	
	public ChatHostController(Stage stage, ServerSocket serverSocket, String serverName, String serverPassword) {
		this.myStage = stage;
		this.serverSocket = serverSocket;
		this.serverName = serverName;
		this.serverPassword = serverPassword;
	}
	
	public void initialize() {
		
		// Attempt to retrieve external IP from Amazon AWS
		String ip_text = "unknown";
		try (Scanner sc = new Scanner(new URL("http://checkip.amazonaws.com/").openStream(), "UTF-8")) {
			sc.useDelimiter("\\A"); // https://community.oracle.com/blogs/pat/2004/10/23/stupid-scanner-tricks
			if (sc.hasNext()) {
				ip_text = sc.next();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ip.setText("IP-address: " + ip_text);
		port.setText("Server port: " + serverSocket.getLocalPort());
		name.setText("Server name: " + serverName);
		password.setText("Server password: " + serverPassword);
	}

}
