package crypto_chat.app.ui.client;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.core.json_models.MessageType;
import crypto_chat.app.core.json_models.json_msg.ChatTextMessage;
import crypto_chat.app.core.json_models.json_msg.WelcomeMessage;
import crypto_chat.app.core.util.GetPackageHeader;
import crypto_chat.app.core.util.RunOnJavaFX;
import crypto_chat.app.core.util.TimedTask;
import crypto_chat.app.ui.server.ClientSocketHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ChatClientController {

	Scene mainMenuScene;
	
	ClientSocketHandler socketHandler;
	String clientName, serverPassword;
	
	@FXML TextField serverIPField, serverPortField, serverHiddenPassword, serverShownPassword; 
	@FXML Label serverNameLabel;
	@FXML Button leaveServerButton;
	@FXML ScrollPane chatRoomScroll;
	@FXML TextArea chatMessageArea;
	@FXML VBox chatRoom;
	
	public ChatClientController(ClientSocketHandler socketHandler, String clientName, String serverPassword) {
		this.socketHandler = socketHandler;
		this.socketHandler.setController(this);
		this.clientName = clientName;
		this.serverPassword = serverPassword;
	}
	
	public void initialize() {
		serverIPField.setText(socketHandler.getIP().toString());
		serverPortField.setText(Integer.toString(socketHandler.getPort()));
		serverHiddenPassword.setText(serverPassword);
		serverShownPassword.setText(serverPassword);
		
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
					sendChatMessageToServer(message, System.currentTimeMillis());
					chatMessageArea.clear();
				}	
			}
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
				// Decode JSON
				JsonElement jsonElement = new JsonParser().parse(msg);
				MessageType type = GetPackageHeader.getPackageHeader(jsonElement);
				Gson gson = new Gson();
				switch(type) {
				case TEXT_MESSAGE:
					ChatTextMessage cm = gson.fromJson(jsonElement, ChatTextMessage.class);
					newMessage(cm.getSenderName(), cm.getMessage(), cm.getTimeStamp());
					break;
				case CLOSED:
					break;
				case WELCOME:
					WelcomeMessage wm = gson.fromJson(jsonElement, WelcomeMessage.class);
					serverNameLabel.setText(wm.getServerName());
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void sendChatMessageToServer(String message, long timestamp) {
		ChatTextMessage cm = new ChatTextMessage(clientName, message, timestamp);
		String json = new Gson().toJson(cm);
		socketHandler.sendMessageToServer(json);
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
	
	public void setMainMenuScene(Scene mainMenuScene) {
		this.mainMenuScene = mainMenuScene;
	}
	
}

