package crypto_chat.app.ui.server;

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
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public StringProperty ip_addressProperty() {
		return this.ip_address;
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public String getIP() {
		return this.ip_address.get();
	}
	
}
