package crypto_chat.app.ui.client;

import java.io.IOException;
import java.net.Socket;

import crypto_chat.app.core.globals.*;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.ui.server.ClientSocketHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JoinServerController {

	private Stage myStage;
	private Scene mainMenuScene;
	private Settings settings;
	
	@FXML Button cancelButton, connectButton;
	@FXML Label statusLabel;
	@FXML CheckBox portCheckbox;
	@FXML TextField clientNameField, serverIPField, serverPasswordField, serverPortField;
	
	String port_field = null;
	
	public JoinServerController(Stage stage) {
		myStage = stage;
	}
	
	public void initialize() {
		
		settings = SettingsFactory.getSettings();
		
		if(settings != null) {
			loadSettings();
		}
		
		
		serverPortField.setText(NetworkDefaults.PORT);
		serverPortField.setDisable(true);
		portCheckbox.setSelected(true);
		connectButton.setDisable(true);
				
		connectButton.setOnAction(ae -> {	
			
			Task<Void> update_task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					disableGUI(true);
					updateMessage("Connecting to " + serverIPField.getText() + " : " + serverPortField.getText() + "...");	
					statusLabel.setStyle("-fx-text-fill:DarkOrange");
					return null;
				}
				
			};
			
			new Thread(update_task).start();;
				
			statusLabel.textProperty().bind(update_task.messageProperty());
			
			update_task.setOnSucceeded(e -> {
				statusLabel.textProperty().unbind();
				String ip_address = serverIPField.getText();
				String port = serverPortField.getText();
				String password = serverPasswordField.getText();
				Socket socket = establishConnection(ip_address, port, password);
				if(socket != null) {
					initializeChatUI(socket);
				}
				else {
					statusLabel.setText("Could connect with " + ip_address + " : " + port);
					statusLabel.setStyle("-fx-text-fill:Red");
					disableGUI(false);
				}
				
			});
			
		});
		
		cancelButton.setOnAction(ae -> {
			if (mainMenuScene != null) {
				((Stage) cancelButton.getScene().getWindow()).setScene(mainMenuScene);
			}
		});
		
		portCheckbox.selectedProperty().addListener((obs, newv, oldv) -> {
			if(newv == false) {
				port_field = serverPortField.getText();
				serverPortField.setText(NetworkDefaults.PORT);
				serverPortField.setDisable(true);
			}
			else {
				serverPortField.setText(port_field == null ? "" : port_field);
				serverPortField.setDisable(false);
				serverPortField.requestFocus();
			}
		});
		
		serverPortField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	String fieldText = serverPortField.getText();
		        if (!newValue.matches("[0-9]")) {
		        	fieldText = newValue.replaceAll("[^0-9]", "");
		        }
		        if(newValue.length() > 5) {
		        	fieldText = newValue.substring(0, 5);
		        }
		        serverPortField.setText(fieldText);
		    }
		});
		
		addValidateListener(serverIPField);
		addValidateListener(serverPasswordField);
		addValidateListener(serverPortField);
		
		ControllerFunctions.buttonActionEnter(connectButton);
		ControllerFunctions.buttonActionEnter(cancelButton);
		ControllerFunctions.fieldFireButton(serverIPField, connectButton);
		ControllerFunctions.fieldFireButton(serverPasswordField, connectButton);
		ControllerFunctions.fieldFireButton(serverPortField, connectButton);
		
		validateFields();
	}
	
	private Socket establishConnection(String ip_address, String port, String password) {
		
		int int_port = 0; 
		try {
			int_port = Integer.valueOf(port); 
		} catch (NumberFormatException e) {
			Alerter.error("Failed to connect", "Port must be an integer in the range 0-65535");
			return null;
		}
		if(int_port < 0 || int_port > 65535) {
			Alerter.error("Failed to connect", "Port must be an integer in the range 0-65535");
			return null;
		}
		
		Socket serversocket;
		try {
			serversocket = new Socket(ip_address, int_port);
		} catch (Exception e) {
			Alerter.exception("Failed to connect", "Could not connect with " + ip_address + " : " + int_port, e);
			return null;
		}
		
		return serversocket;
	}
	
	private void initializeChatUI(Socket serversocket) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatClient.fxml"));
		ClientSocketHandler socketHandler = new ClientSocketHandler(serversocket);
		ChatClientController controller = new ChatClientController(socketHandler, clientNameField.getText(), serverPasswordField.getText());
		controller.setMainMenuScene(mainMenuScene);
		loader.setController(controller);
		
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root);
		myStage.setScene(scene);
	}
		
	private void validateFields() {
		if(serverIPField.getText().equals("") || serverPasswordField.getText().equals("") || serverPortField.getText().equals("")) {
			connectButton.setDisable(true);
		}
		else{
			connectButton.setDisable(false);
		}
	}
	
	private void addValidateListener(TextField field) {
		field.textProperty().addListener((obs, newv, oldv) -> {
			validateFields();
		});
	}
	
	private void disableGUI(boolean disable) {
		serverIPField.setDisable(disable);
		serverPasswordField.setDisable(disable);
		serverPortField.setDisable(portCheckbox.isSelected() ? true : disable);
		portCheckbox.setDisable(disable);
		connectButton.setDisable(disable);
		cancelButton.setDisable(disable);
	}
	
	private void loadSettings() {
		String saveName = settings.getClient_name();
		String saveIP = settings.getIp_address();
		serverIPField.setText(saveIP);
		clientNameField.setText(saveName);
		
		if("".equals(saveName)) {
			Platform.runLater(() -> {
				clientNameField.requestFocus();
			});
		} else if("".equals(saveIP)) {
			Platform.runLater(() -> {
				serverIPField.requestFocus();
			});
		} else {
			Platform.runLater(() -> {
				serverPasswordField.requestFocus();
			});
		}
	}
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}
}
