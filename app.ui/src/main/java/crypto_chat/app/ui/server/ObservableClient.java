package crypto_chat.app.ui.server;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.Gson;

import crypto_chat.app.core.json_models.json_msg.ChatFileMessage;
import crypto_chat.app.core.json_models.json_msg.ChatImageMessage;
import crypto_chat.app.core.json_models.json_msg.ChatMessage;
import crypto_chat.app.core.json_models.json_msg.ChatMessageLog;
import crypto_chat.app.core.json_models.json_msg.ChatTextMessage;
import crypto_chat.app.core.json_models.json_msg.WelcomeMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class ObservableClient {
	
	private ClientThread client;
	
	private StringProperty name = new SimpleStringProperty();
	private StringProperty ip_address = new SimpleStringProperty();

	public ObservableClient(ClientThread client) {
		this.client = client;
		this.name.setValue(client.getName());
		this.ip_address.setValue(client.getIP());
	}
	
	public void sendJSONMessage(String json) {
		this.client.sendMessageToClient(json);
	}
	
	public void sendWelcomeMessage(String serverName) {
		WelcomeMessage wm = new WelcomeMessage(serverName, null);
		String json = new Gson().toJson(wm);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatTextMessage(String clientName, String message, long timestamp) {
		ChatTextMessage cm = new ChatTextMessage(clientName, message, timestamp);
		String json = new Gson().toJson(cm);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatImageMessage(String clientName, Image image, long timestamp) {
		ChatImageMessage im = new ChatImageMessage(clientName, image, timestamp);
		String json = new Gson().toJson(im);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatFileMessage(String clientName, File file, long timestamp) {
		ChatFileMessage fm = new ChatFileMessage(clientName, file, timestamp);
		String json = new Gson().toJson(fm);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatMessageLog(ArrayList<ChatMessage> chatMessageLog) {
		ChatMessageLog ml = new ChatMessageLog(chatMessageLog);
		String json = new Gson().toJson(ml);
		this.client.sendMessageToClient(json);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public StringProperty ip_addressProperty() {
		return this.ip_address;
	}
	
	public void setName(String name) {
		this.name.set(name);
		this.client.setName(name);
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public String getIP() {
		return this.ip_address.get();
	}
	
}
