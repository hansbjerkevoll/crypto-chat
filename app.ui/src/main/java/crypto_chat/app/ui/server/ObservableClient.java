package crypto_chat.app.ui.server;

import java.io.File;
import com.google.gson.Gson;

import crypto_chat.app.core.json_models.json_msg.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
	
	public void sendChatTextMessage(String clientName, String message, long timestamp) {
		ChatMessageText cm = new ChatMessageText(clientName, message, timestamp);
		String json = new Gson().toJson(cm);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatImageMessage(String clientName, byte[] image_bytes, long timestamp) {
		ChatMessageImage im = new ChatMessageImage(clientName, image_bytes, timestamp);
		String json = new Gson().toJson(im);
		this.client.sendMessageToClient(json);
	}
	
	public void sendChatFileMessage(String clientName, File file, long timestamp) {
		ChatMessageFile fm = new ChatMessageFile(clientName, file, timestamp);
		String json = new Gson().toJson(fm);
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
