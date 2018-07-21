package crypto_chat.app.ui.host;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import crypto_chat.app.core.globals.ChatFunctions;
import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.core.json_models.MessageType;
import crypto_chat.app.core.json_models.json_msg.ChatMessageText;
import crypto_chat.app.core.json_models.json_msg.ClientConnectionRequest;
import crypto_chat.app.core.json_models.json_msg.ClientConnectionResponse;
import crypto_chat.app.core.json_models.json_msg.ConnectionClosed;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.core.util.ChatHistory;
import crypto_chat.app.core.util.GetPackageHeader;
import crypto_chat.app.core.util.RunOnJavaFX;
import crypto_chat.app.core.util.TimedTask;
import crypto_chat.app.ui.server.ChatServer;
import crypto_chat.app.ui.server.ClientThread;
import crypto_chat.app.ui.server.ObservableClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class ChatHostController {
	
	@FXML TextField serverIPField, serverPortField, serverShownPassword;
	@FXML PasswordField serverHiddenPassword;
	@FXML MenuItem changeRoomButton, saveHistoryButton, closeServerButton;
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
	private ArrayList<String> clientNames = new ArrayList<>();
	private ArrayList<String> chatHistory = new ArrayList<>();
	
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
		
		clientNames.add(hostName);
	
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
					ChatFunctions.newTextMessage(chatRoom, hostName, message, timestamp);
					TimedTask.runLater(new Duration(30), () -> {
						chatRoomScroll.setVvalue(1.0);
					});
					sendNewTextMessage(hostName, message, timestamp);
					chatMessageArea.clear();
				}	
			}
		});
		
		closeServerButton.setOnAction(ae -> {
			if (closeServer() && mainMenuScene != null) {
				myStage.setOnCloseRequest(null);
				myStage.setScene(mainMenuScene);
			}
		});
		
		saveHistoryButton.setOnAction(ae -> {
			FileChooser filechooser = new FileChooser();
			filechooser.setTitle("Save Chat History");
			ExtensionFilter filter = new ExtensionFilter("Text File", "*.txt");
			filechooser.getExtensionFilters().add(filter);
			filechooser.setSelectedExtensionFilter(filter);
			File file = filechooser.showSaveDialog(myStage);
			if(file != null) {
				try {
					ChatHistory.saveHistoryToLocalFile(chatHistory, file.getAbsolutePath());
					Alerter.info("Chat history saved", "The chat history was saved to the local file: " + file.getAbsolutePath());
				} catch (IOException e) {
					Alerter.exception("Saving failed", "Could not save chat history to local file", e);
				}
			}
		});
		
		myStage.setOnCloseRequest(e -> {
			e.consume();
			if (closeServer() && mainMenuScene != null) {
				myStage.close();
			}
		});
		
		// Load Chat Log
		if(chatHistory != null) {
			Gson gson = new Gson();
			for(String chat_msg : chatHistory) {
				JsonElement jsonElement = new JsonParser().parse(chat_msg);
				MessageType type = GetPackageHeader.getPackageHeader(jsonElement);
				switch(type) {
				case TEXT_MESSAGE:
					ChatMessageText text_msg = gson.fromJson(jsonElement, ChatMessageText.class);
					ChatFunctions.newTextMessage(chatRoom, text_msg.getSender(), text_msg.getTextMessage(), text_msg.getTimeStamp());
					break;
				default:
					break;
				}	
			}
			TimedTask.runLater(new Duration(30), () -> {
				chatRoomScroll.setVvalue(1.0);
			});	
		}
		
		Platform.runLater(() -> {
			chatMessageArea.requestFocus();
		});
		
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
					ChatMessageText cm = gson.fromJson(jsonElement, ChatMessageText.class);
					chatHistory.add(msg);
					ChatFunctions.newTextMessage(chatRoom, cm.getSender(), cm.getTextMessage(), cm.getTimeStamp());
					TimedTask.runLater(new Duration(30), () -> {
						chatRoomScroll.setVvalue(1.0);
					});
					forwardChatMessage(msg, cm.getSender());
					break;
				case CONNECTION_CLOSED:
					removeClient(clientThread.getObservableClient());
					clientThread.disconnectClient();
					break;
				case CONNECTION_REQUEST:
					ClientConnectionRequest request = gson.fromJson(jsonElement, ClientConnectionRequest.class);
					clientThread.setName(request.getClientName());
					boolean accepted = false;
					boolean available_name = checkNameAvailability(request.getClientName());
					boolean correct_pwd = request.getHashedPassword().equals(serverPassword);
					String response_msg = "Access denied";
					
					if(!available_name) {
						response_msg = "Client name, " + request.getClientName() + ", already taken. Please choose another";
					} else if (!correct_pwd) {
						response_msg = "Incorrect password";
					} 
					
					if(available_name && correct_pwd) {
						accepted = true;
						response_msg = "Access granted";
						ObservableClient client = new ObservableClient(clientThread);
						clientThread.setObservableClient(client);
						clients.add(client);
						String newConnection = request.getClientName() + " (" + clientThread.getIP() + ") connected.";
						newUpdate(newConnection);
					} else {
						clientThread.disconnectClient();
					}
					
					ClientConnectionResponse response = new ClientConnectionResponse(accepted, response_msg);
					if(accepted) {
						response.setServername(serverName);
						response.setChatMessageLog(chatHistory);
					}
					String json = new Gson().toJson(response);
					clientThread.sendMessageToClient(json);
					break;
				default:
					System.out.println("Unknown message type received: " + type);
					System.out.println("Message was: " + msg);
					break;
				}
				
			}
		});
	}
	
	public void removeClient(ObservableClient client) {
		RunOnJavaFX.run(() -> {
			if(!clients.contains(client)) {
				return;
			}
			newUpdate(client.getName() + " disconnected.");
			clients.remove(client);
		});
		
	}
	
	private void newUpdate(String updateText) {
		listviewUpdates.getItems().add(0, updateText);
	}
	
	public void sendNewTextMessage(String name, String message, long timestamp) {
		ChatMessageText cm = new ChatMessageText(name, message, timestamp);
		String json = new Gson().toJson(cm);
		chatHistory.add(json);
		sendJSONToAllClients(json);
	}
	
	public void forwardChatMessage(String msg, String sender_name) {
		for(ObservableClient client : clients) {
			if(!sender_name.equals(client.getName())) {
				client.sendJSONMessage(msg);
			}
		}
	}
	
	public void sendJSONToAllClients(String json) {
		for(ObservableClient client : clients) {
			client.sendJSONMessage(json);
		}
	}
	
	private boolean closeServer() {
		Optional<ButtonType> result = Alerter.confirmation("Close server", "Are you sure you wanna close the server?");
		if(result.get() == ButtonType.OK) {
			ConnectionClosed cc = new ConnectionClosed();
			String json = new Gson().toJson(cc);
			sendJSONToAllClients(json);
			try {
				ControllerFunctions.stopServer();
			} catch (InterruptedException e) {
				Alerter.exception(null, "An exception occured when closing the server", e);
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				Alerter.exception(null, "An exception occured when closing the server", e);
			}
			return true;
		} 
		return false;
	}
	
	private boolean checkNameAvailability(String name) {
		for(String clientName : clientNames) {
			if(clientName.equals(name)) {
				return false;
			}
		}
		return true;
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
	
	public void setChatHistory(ArrayList<String> chatHistory) {
		this.chatHistory = chatHistory;
	}
	
	public void setMainMenuScene(Scene mainMenuScene) {
		this.mainMenuScene = mainMenuScene;
	}

}
