package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.core.util.RunOnJavaFX;
import crypto_chat.app.ui.MainMenuController;
import crypto_chat.app.ui.server.ChatServer;
import crypto_chat.app.ui.server.ClientThread;
import crypto_chat.app.ui.server.ObservableClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatLobbyHostController {
	
	@FXML TextField serverIPField, serverPortField, serverShownPassword;
	@FXML PasswordField serverHiddenPassword;
	@FXML Button changeRoomButton, closeServerButton;
	@FXML Label serverNameLabel;
	@FXML TableView<ObservableClient> tableviewClients;
	@FXML TableColumn<ObservableClient, String> tablecolumnName;
	@FXML TableColumn<ObservableClient, String> tablecolumnIP;
	@FXML ListView<String> listviewUpdates;
	@FXML HBox lobbyHBox, chatHBox;
	@FXML VBox chatRoom;
	@FXML ScrollPane chatRoomScroll;
	
	private Stage myStage;
	
	private ServerSocket serverSocket;
	private String serverName;
	private String serverPassword;
	
	private ChatServer chatServer;
	private Thread chatServerThread;
	
	private ObservableList<ObservableClient> clients = FXCollections.observableArrayList();
	
	public ChatLobbyHostController(Stage stage, ServerSocket serverSocket, String serverName, String serverPassword) {
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
		
		chatHBox.setVisible(false);
		chatHBox.setManaged(false);
		
		chatServer = new ChatServer(this, serverSocket, serverName, serverPassword);
		chatServerThread = new Thread(chatServer, "ListenerServerThread");
		chatServerThread.start();
		Threads.THREADS.add(chatServerThread);		
	
		tableviewClients.setItems(clients);
		tablecolumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tablecolumnIP.setCellValueFactory(new PropertyValueFactory<>("ip_address"));
		
		serverHiddenPassword.setOnMouseEntered(me -> {
			serverHiddenPassword.setVisible(false);
			serverHiddenPassword.setManaged(false);
			serverShownPassword.setVisible(true);
			serverShownPassword.setManaged(true);
		});
		
		serverShownPassword.setOnMouseExited(me -> {
			serverShownPassword.setVisible(false);
			serverShownPassword.setManaged(false);
			serverHiddenPassword.setVisible(true);
			serverHiddenPassword.setManaged(true);
		});
		
		changeRoomButton.setOnAction(ae -> {
			chatHBox.setVisible(!chatHBox.isVisible());
			chatHBox.setManaged(!chatHBox.isManaged());
			lobbyHBox.setVisible(!lobbyHBox.isVisible());
			lobbyHBox.setManaged(!lobbyHBox.isManaged());
			changeRoomButton.setText(chatHBox.isVisible() ? "Go to Chat Lobby" : "Go to Chat Room");
		});
		
		closeServerButton.setOnAction(ae -> {
			closeServer();
		});
		
		ControllerFunctions.buttonActionEnter(changeRoomButton);
		ControllerFunctions.buttonActionEnter(closeServerButton);
		
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
	
	public void connectClient(ObservableClient client) {
		RunOnJavaFX.run(() -> {
			String newConnection = client.getName() + " (" + client.getIP() + ") connected.";
			newUpdate(newConnection);
			clients.add(client);
		});
	}
	
	private void newUpdate(String updateText) {
		listviewUpdates.getItems().add(0, updateText);
	}
	
	private void newMessage(String header, String message) {
		VBox messageBox = new VBox();
		Label nameLabel = new Label(header);
		Label messageLabel = new Label(message);
		messageBox.setMaxWidth(775);
		messageBox.setPadding(new Insets(2));
		nameLabel.setWrapText(true);
		messageLabel.setWrapText(true);
		nameLabel.setStyle("-fx-font-weight:Bold");
		messageBox.getChildren().addAll(nameLabel, messageLabel);
		chatRoom.getChildren().add(messageBox);
		chatRoomScroll.setVvalue(1.0);
	}
	
	public void gotMessageFromClient(ClientThread clientThread) {
		
	}
	
	public void removeClient(ObservableClient observableClient) {
		
	}
	
	private void closeServer() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Close server");
		alert.setContentText("Are you sure you wanna close the server?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				Alerter.exception(null, "An exception occured when closing the server", e);
			}
			try {
				ControllerFunctions.stopServer();
			} catch (InterruptedException e) {
				Alerter.exception(null, "An exception occured when closing the server", e);
			}
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../MainMenu.fxml"));
			MainMenuController controller = new MainMenuController(myStage);
			loader.setController(controller);
			
			Parent root;
			try {
				root = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
				Alerter.exception("Failed to connect", "Error occured while loading the ChatLobby UI", e);	
				return;
			}
			Scene scene = new Scene(root);
			myStage.setScene(scene);
		}   
	}

}
