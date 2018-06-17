package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.ServerSocket;

import crypto_chat.app.ui.globals.NetworkDefaults;
import crypto_chat.app.ui.globals.ResourceLocations;
import crypto_chat.app.ui.util.Alerter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class HostServerController {
	
	Stage myStage;
	Scene mainMenuScene;
	
	@FXML Button cancelButton, hostButton;
	@FXML Label statusLabel;
	@FXML CheckBox portCheckbox;
	@FXML TextField serverNameField, serverPasswordField, serverPortField;
	
	public HostServerController(Stage stage) {
		myStage = stage;
	}
	
	@FXML public void initialize() {
		
		serverPortField.setText(NetworkDefaults.PORT);
		serverPortField.setDisable(true);
		portCheckbox.setSelected(true);
		hostButton.setDisable(true);
		
		addValidateListener(serverNameField);
		addValidateListener(serverPasswordField);
		addValidateListener(serverPortField);
		
		hostButton.setOnAction(ae -> {
			disableGUI();
			updateStatusLabel("Connecting...", true);
			if(!establishConnection(serverNameField.getText(), serverPasswordField.getText(), serverPortField.getText())) {
				enableGUI();
				updateStatusLabel("Ready to connect...", false);
			};
		});
		
		portCheckbox.selectedProperty().addListener((obs, newv, oldv) -> {
			if(!newv) {
				serverPortField.setText(NetworkDefaults.PORT);
				serverPortField.setDisable(true);
			}
			else {
				serverPortField.clear();;
				serverPortField.setDisable(false);
			}
		});
		
		cancelButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				cancelButton.fire();
			}
		});
		
		cancelButton.setOnAction(ae -> {
			if (mainMenuScene != null) {
				((Stage) cancelButton.getScene().getWindow()).setScene(mainMenuScene);
			}
		});
	}
	
	private boolean establishConnection(String servername, String password, String port) {
		int int_port = 0; 
		try {
			int_port = Integer.valueOf(port); 
		} catch (NumberFormatException e) {
			Alerter.error("Failed to connect", "Port must be an integer in the range 0-65535");
			return false;
		}
		if(int_port < 0 || int_port > 65535) {
			Alerter.error("Failed to connect", "Port must be an integer in the range 0-65535");
			return false;
		}
		
		ServerSocket serversocket;
		try {
			serversocket = new ServerSocket(int_port);
			serversocket.setSoTimeout(1000);
		}catch (IOException e) {
			Alerter.exception("Failed to connect", "Error when creating socket", e);
			return false;
		}
		
		ChatHostController controller = new ChatHostController(myStage, serversocket, servername, password);
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ResourceLocations.FXML_CHAT_HOST));
		loader.setController(controller);
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			Alerter.exception("Failed to connect", "Error encountered while creating the lobby UI", e);
			return false;
		}
		Scene s = new Scene(root);
		myStage.setScene(s);
		myStage.centerOnScreen();
		
		return true;
	}
	
	private void validateFields() {
		if(serverNameField.getText().equals("") || serverPasswordField.getText().equals("") || serverPortField.getText().equals("")) {
			hostButton.setDisable(true);
		}
		else{
			hostButton.setDisable(false);
		}
	}
	
	private void addValidateListener(TextField field) {
		field.textProperty().addListener((obs, newv, oldv) -> {
			validateFields();
		});
	}
	
	private void disableGUI() {
		serverNameField.setDisable(true);
		serverPasswordField.setDisable(true);
		serverPortField.setDisable(true);
		portCheckbox.setDisable(true);
		hostButton.setDisable(true);
		cancelButton.setDisable(true);
	}
	
	private void enableGUI() {
		serverNameField.setDisable(false);
		serverPasswordField.setDisable(false);
		serverPortField.setDisable(false);
		portCheckbox.setDisable(portCheckbox.isSelected());
		hostButton.setDisable(false);
		cancelButton.setDisable(false);
	}
	
	private void updateStatusLabel(String text, boolean connecting) {
		statusLabel.setText(text);
		statusLabel.setStyle(connecting ? "-fx-text-fill:DarkOrange" : "-fx-text-fill:DarkGreen");
	}
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}

}
