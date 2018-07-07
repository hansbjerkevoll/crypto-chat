package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.core.json_models.MessageType;
import crypto_chat.app.core.json_models.json_msg.ChatMessage;
import crypto_chat.app.core.json_models.json_msg.ChatTextMessage;
import crypto_chat.app.core.json_models.json_msg.ClientConnectionRequest;
import crypto_chat.app.core.json_models.json_msg.ClientConnectionResponse;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.core.util.RunOnJavaFX;
import crypto_chat.app.core.util.TimedTask;
import crypto_chat.app.core.util.GetPackageHeader;
import crypto_chat.app.ui.server.ChatServer;
import crypto_chat.app.ui.server.ClientThread;
import crypto_chat.app.ui.server.ObservableClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChatHostController {
	
	@FXML TextField serverIPField, serverPortField, serverShownPassword;
	@FXML PasswordField serverHiddenPassword;
	@FXML MenuItem changeRoomButton, closeServerButton;
	@FXML Label serverNameLabel, serverRoomLabel;
	@FXML TableView<ObservableClient> tableviewClients;
	@FXML TableColumn<ObservableClient, String> tablecolumnName;
	@FXML TableColumn<ObservableClient, String> tablecolumnIP;
	@FXML ListView<String> listviewUpdates;
	@FXML HBox lobbyHBox;
	@FXML VBox chatRoom, chatVBox;
	@FXML ScrollPane chatRoomScroll;
	@FXML TextArea chatMessageArea;
	
	private Stage myStage;
	private Scene mainMenuScene;
	
	private ServerSocket serverSocket;
	private String hostName, serverName, serverPassword;
	
	private ChatServer chatServer;
	private Thread chatServerThread;
	
	private ObservableList<ObservableClient> clients = FXCollections.observableArrayList();
	private ArrayList<ChatMessage> chatHistory = new ArrayList<>();
	
	public ChatHostController(Stage stage, ServerSocket serverSocket, String hostName, String serverName, String serverPassword) {
		this.myStage = stage;
		this.serverSocket = serverSocket;
		this.hostName = hostName;
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
		
		lobbyHBox.setVisible(false);
		lobbyHBox.setManaged(false);
		
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
			chatVBox.setVisible(!chatVBox.isVisible());
			chatVBox.setManaged(!chatVBox.isManaged());
			lobbyHBox.setVisible(!lobbyHBox.isVisible());
			lobbyHBox.setManaged(!lobbyHBox.isManaged());
			changeRoomButton.setText(chatVBox.isVisible() ? "Go to Chat Lobby" : "Go to Chat Room");
			serverRoomLabel.setText(chatVBox.isVisible() ? "Chat Server Room" : "Chat Server Lobby");
			if(chatVBox.isVisible())chatMessageArea.requestFocus();
		});
		
		chatMessageArea.setOnKeyPressed(ke -> {
			String message = chatMessageArea.getText().trim();
			if(ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				if(ke.isShiftDown()) {
					chatMessageArea.setText(chatMessageArea.getText() + "\n");
					chatMessageArea.positionCaret(chatMessageArea.getText().length());
				} else if(!message.equals("")) {
					long timestamp = System.currentTimeMillis();
					newMessage(hostName, message, timestamp);
					sendNewMessage(hostName, message, timestamp);
					chatMessageArea.clear();
				}	
			}
		});
		
		closeServerButton.setOnAction(ae -> {
			closeServer();
		});
		
	}
	
	public void sendNewMessage(String name, String message, long timestamp) {
		ChatTextMessage cm = new ChatTextMessage(name, message, timestamp);
		String json = new Gson().toJson(cm);
		sendJSONToAllClients(json);
	}
	
	public void sendJSONToAllClients(String json) {
		for(ObservableClient client : clients) {
			client.sendJSONMessage(json);
		}
	}
	
	public void gotMessageFromClient(ClientThread clientThread) {
		RunOnJavaFX.run(() -> {
			String msg;
			while ((msg = clientThread.getMessage()) != null) {
				// Decode JSON
				JsonElement jsonElement = new JsonParser().parse(msg);
				MessageType type = GetPackageHeader.getPackageHeader(jsonElement);
				Gson gson = new Gson();
				switch(type) {
				case TEXT_MESSAGE:
					ChatTextMessage cm = gson.fromJson(jsonElement, ChatTextMessage.class);
					chatHistory.add(cm);
					newMessage(cm.getSenderName(), cm.getMessage(), cm.getTimeStamp());
					sendJSONToAllClients(msg);
					break;
				case CLOSED:
					break;
				case WELCOME:
					break;
				case CONNECTION_REQUEST:
					ClientConnectionRequest request = gson.fromJson(jsonElement, ClientConnectionRequest.class);
					boolean accepted = false;
					clientThread.setName(request.getClientName());
					if(request.getHashedPassword().equals(serverPassword)) {
						accepted = true;
						ObservableClient client = new ObservableClient(clientThread);
						clientThread.setObservableClient(client);
						clients.add(client);
						String newConnection = request.getClientName() + " (" + clientThread.getIP() + ") connected.";
						newUpdate(newConnection);
					}
					else {
						try {
							clientThread.disconnectClient();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					ClientConnectionResponse response = new ClientConnectionResponse(accepted, serverName);
					String json = new Gson().toJson(response);
					clientThread.sendMessageToClient(json);
					break;
				default:
					break;
				}
				
			}
		});
	}
	
	public void removeClient(ObservableClient observableClient) {
		
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
	
	private void newUpdate(String updateText) {
		listviewUpdates.getItems().add(0, updateText);
	}
	
	private void newMessage(String header, String message, long timestamp) {
		
		Date msg_time = new Date(timestamp);
		VBox messageBox = new VBox();
		Label nameLabel = new Label(header + " [" + msg_time + "]");
		Label messageLabel = new Label(message);
		messageBox.setMaxWidth(775);
		messageBox.setPadding(new Insets(2));
		nameLabel.setWrapText(true);
		messageLabel.setWrapText(true);
		nameLabel.setStyle("-fx-font-weight:Bold");
		messageBox.getChildren().addAll(nameLabel, messageLabel);
		chatRoom.getChildren().add(messageBox);
		TimedTask.runLater(new Duration(30), () -> {
			chatRoomScroll.setVvalue(1.0);
		});	
	}
	
	private void closeServer() {
		Optional<ButtonType> result = Alerter.confirmation("Close server", "Are you sure you wanna close the server?");
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
			
			if (mainMenuScene != null) {
				myStage.setScene(mainMenuScene);
			}
		}   
	}
	
	public void setMainMenuScene(Scene mainMenuScene) {
		this.mainMenuScene = mainMenuScene;
	}

}
