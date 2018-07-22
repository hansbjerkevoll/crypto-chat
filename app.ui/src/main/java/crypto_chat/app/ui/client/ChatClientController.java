package crypto_chat.app.ui.client;

import crypto_chat.app.core.globals.*;
import crypto_chat.app.core.json_models.MessageType;
import crypto_chat.app.core.json_models.json_msg.*;
import crypto_chat.app.core.security.AES;
import crypto_chat.app.core.security.SHA_512;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.*;
import crypto_chat.app.ui.server.ClientSocketHandler;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class ChatClientController {
	
	private Stage myStage;
	private Scene mainMenuScene;
	
	private AES aes;
	private Settings settings;
	private String directoryPath;
	
	private ClientSocketHandler socketHandler;
	private String clientName, serverName, ip_address, port, serverPassword, serverPasswordHash;
	
	private ArrayList<String> chatHistory;
	
	private ArrayList<String> sentMessages = new ArrayList<>();
	private int sm_index = -1;
	
	@FXML TextField serverIPField, serverPortField, serverHiddenPassword, serverShownPassword; 
	@FXML Label serverNameLabel;
	@FXML ScrollPane chatRoomScroll;
	@FXML TextArea chatMessageArea;
	@FXML VBox chatRoom;
	@FXML MenuItem saveHistoryButton, leaveServerButton;
	
	public ChatClientController(Stage stage, ClientSocketHandler socketHandler, String clientName, String serverName, String ip_address, String port, String serverPassword) {
		this.myStage = stage;
		this.socketHandler = socketHandler;
		this.socketHandler.setController(this);
		this.clientName = clientName;
		this.serverName = serverName;
		this.ip_address = ip_address;
		this.port = port;
		this.serverPassword = serverPassword;
		try {
			this.serverPasswordHash = SHA_512.generateHashedPassword_SHA_512(serverPassword, null);
			this.aes = new AES(Arrays.copyOfRange(serverPasswordHash.getBytes(), 0, 48));
		} catch (NoSuchAlgorithmException e) {
			// Do nothing, will never occur
		}
	}
	
	public void initialize() {
		
		settings = SettingsFactory.getSettings();
		
		if(settings != null) {
			String historyLocation = settings.getHistory_location();
			directoryPath = historyLocation == null || "".equals(historyLocation) ? null : historyLocation;
		}
		
		serverIPField.setText(ip_address);
		serverPortField.setText(port);
		serverHiddenPassword.setText(serverPassword);
		serverShownPassword.setText(serverPassword);
		serverNameLabel.setText(serverName);
		
		serverShownPassword.setVisible(false);
		serverShownPassword.setManaged(false);
		
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
		
		chatMessageArea.setOnKeyPressed(ke -> {
			String message = chatMessageArea.getText().trim();
			if(ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				if(ke.isShiftDown()) {
					chatMessageArea.setText(chatMessageArea.getText() + "\n");
					chatMessageArea.positionCaret(chatMessageArea.getText().length());
				} else if(!message.equals("")) {
					long timestamp = System.currentTimeMillis();
					sendChatTextMessageToServer(message, timestamp);
					ChatFunctions.newTextMessage(chatRoom, clientName, message, timestamp);
					if(sentMessages.size() > 0) {
						if(!sentMessages.get(0).equals(message)) sentMessages.add(0, message);
					} else {
						sentMessages.add(0, message);
					}
					sm_index = -1;
					TimedTask.runLater(new Duration(30), () -> {
						chatRoomScroll.setVvalue(1.0);
					});	
					chatMessageArea.clear();
				}	
			} else if(ke.getCode() == KeyCode.UP) {
				if(sentMessages.size() > sm_index + 1) {
					sm_index++;
					chatMessageArea.setText(sentMessages.get(sm_index));
					chatMessageArea.positionCaret(chatMessageArea.getText().length());
				}
			} else if(ke.getCode() == KeyCode.DOWN) {
				System.out.println(sentMessages.size() + " " +  sm_index);
				if(sm_index > 0) {
					sm_index--;
					chatMessageArea.setText(sentMessages.get(sm_index));
					chatMessageArea.positionCaret(chatMessageArea.getText().length());
				}
			}
		});
		
		leaveServerButton.setOnAction(ae -> {
			if(leaveServer() && mainMenuScene != null) {
				myStage.setOnCloseRequest(null);
				myStage.setScene(mainMenuScene);
			}
		});
		
		myStage.setOnCloseRequest(e -> {
			e.consume();
			if (leaveServer()) {
				myStage.close();
			}
		});
		
		saveHistoryButton.setOnAction(ae -> {
			FileChooser filechooser = new FileChooser();
			filechooser.setTitle("Save Chat History");
			ExtensionFilter filter = new ExtensionFilter("Text File", "*.txt");
			filechooser.getExtensionFilters().add(filter);
			filechooser.setSelectedExtensionFilter(filter);
			if(directoryPath != null) {
				filechooser.setInitialDirectory(new File(directoryPath));
			}
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
		
		// Start the socket thread
		Thread t = new Thread(socketHandler, "ClientSocketThread");
		t.start();
		Threads.THREADS.add(t);
	}
	
	public void gotMessageFromServer() {
		RunOnJavaFX.run(() -> {
			String msg;
			while ((msg = socketHandler.getMessage()) != null) {
				// Decrypt msg
				msg= new String(aes.triple_AES_decrypt(DatatypeConverter.parseHexBinary(msg)));
				
				// Decode JSON
				JsonElement jsonElement = new JsonParser().parse(msg);
				MessageType type = GetPackageHeader.getPackageHeader(jsonElement);
				Gson gson = new Gson();
				switch(type) {
				case TEXT_MESSAGE:
					ChatMessageText cm = gson.fromJson(jsonElement, ChatMessageText.class);
					ChatFunctions.newTextMessage(chatRoom, cm.getSender(), cm.getTextMessage(), cm.getTimeStamp());
					TimedTask.runLater(new Duration(30), () -> {
						chatRoomScroll.setVvalue(1.0);
					});	
					break;
				case KICKED:
					serverNameLabel.setText("You have been kick from the server, and will return to the main menu in 5 seconds");
					serverNameLabel.setStyle("-fx-text-fill:Red");
					socketHandler.disconnect();
					chatMessageArea.setDisable(true);
					TimedTask.runLater(new Duration(5000), () -> {
						myStage.setOnCloseRequest(null);
						myStage.setScene(mainMenuScene);
					});
					break;
				case MUTED:
					MuteClient mc = gson.fromJson(jsonElement, MuteClient.class);
					if(mc.getIsMuted()) {
						serverNameLabel.setText("You have been muted by the host");
						serverNameLabel.setStyle("-fx-text-fill:Red");
						chatMessageArea.setDisable(true);
					} else {
						serverNameLabel.setText("You have been unmuted by the host");
						serverNameLabel.setStyle("-fx-text-fill:Green");
						chatMessageArea.setDisable(false);
						TimedTask.runLater(new Duration(5000), () -> {
							serverNameLabel.setText(serverName);
							serverNameLabel.setStyle("-fx-text-fill:Black");
						});
					}
					break;
				case CONNECTION_CLOSED:
					serverNameLabel.setText("The server was closed by the host");
					serverNameLabel.setStyle("-fx-text-fill:Red");
					chatMessageArea.setDisable(true);
					socketHandler.disconnect();
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void sendChatTextMessageToServer(String message, long timestamp) {
		ChatMessageText cm = new ChatMessageText(clientName, message, timestamp);
		String json = new Gson().toJson(cm);
		socketHandler.sendMessageToServer(json, aes);
	}
	
	public void setChatMessageLog(ArrayList<String> chatMessageLog) {
		this.chatHistory = chatMessageLog;
	}
	
	private boolean leaveServer() {
		if(socketHandler.isDisconnected()) {
			return true;
		}
		Optional<ButtonType> result = Alerter.confirmation("Leave server?", "Are you sure you wanna leave the server?");
		if(result.get() == ButtonType.OK) {
			ConnectionClosed cc = new ConnectionClosed();
			String json = new Gson().toJson(cc);
			socketHandler.sendMessageToServer(json, aes);
			socketHandler.disconnect();
			return true;
		}
		return false;
	}
	
	public void setMainMenuScene(Scene mainMenuScene) {
		this.mainMenuScene = mainMenuScene;
	}
	
}

