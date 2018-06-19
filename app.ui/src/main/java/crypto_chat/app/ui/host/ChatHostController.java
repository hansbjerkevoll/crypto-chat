package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatHostController {
	
	@FXML TextField serverIPField, serverPortField, serverShownPassword;
	@FXML PasswordField serverHiddenPassword;
	@FXML Button toChatButton;
	@FXML Label serverNameLabel;
	@FXML TableView<String> tableviewClients;
	@FXML TableColumn<String, String> tablecolumnName, tableviewIP;
	
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
		
		serverIPField.setText(getExternalIP());
		serverPortField.setText(Integer.toString(serverSocket.getLocalPort()));
		serverHiddenPassword.setText(serverPassword);
		serverShownPassword.setText(serverPassword);
		serverNameLabel.setText(serverName);
		
		serverShownPassword.setVisible(false);
		serverShownPassword.setManaged(false);
		
		serverHiddenPassword.setOnMouseEntered(me -> {
			serverShownPassword.setVisible(true);
			serverShownPassword.setManaged(true);
			serverHiddenPassword.setVisible(false);
			serverHiddenPassword.setManaged(false);
		});
		
		serverShownPassword.setOnMouseExited(me -> {
			serverShownPassword.setVisible(false);
			serverShownPassword.setManaged(false);
			serverHiddenPassword.setVisible(true);
			serverHiddenPassword.setManaged(true);
		});
				
	}
		
	private String getExternalIP() {
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
		return ip_text;
	}

}
