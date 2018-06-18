package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.ServerSocket;

import crypto_chat.app.core.globals.NetworkDefaults;
import crypto_chat.app.core.globals.ResourceLocations;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.core.util.TimedTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Duration;

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
			disableGUI(true);
			updateStatusLabel("Setting up server...", "DarkOrange");
			if(!establishConnection(serverNameField.getText(), serverPasswordField.getText(), serverPortField.getText())) {
				TimedTask.runLater(Duration.millis(500), () -> {
					disableGUI(false);
					updateStatusLabel("Ready to host server...", "DarkGreen");
				});
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
	
	private void disableGUI(boolean disable) {
		serverNameField.setDisable(disable);
		serverPasswordField.setDisable(disable);
		serverPortField.setDisable(disable);
		portCheckbox.setDisable(disable);
		hostButton.setDisable(disable);
		cancelButton.setDisable(disable);
	}
	
	private void updateStatusLabel(String text, String color) {
		statusLabel.setText(text);
		statusLabel.setStyle("-fx-text-fill:"+color);
	}
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}

}
