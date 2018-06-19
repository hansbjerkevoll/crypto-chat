package crypto_chat.app.ui.client;

import java.io.IOException;
import java.net.Socket;

import crypto_chat.app.core.globals.NetworkDefaults;
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

public class JoinServerController {

	Stage myStage;
	Scene mainMenuScene;
	
	@FXML Button cancelButton, connectButton;
	@FXML Label statusLabel;
	@FXML CheckBox portCheckbox;
	@FXML TextField serverIPField, serverPasswordField, serverPortField;
	
	String port_field = null;
	
	public JoinServerController(Stage stage) {
		myStage = stage;
	}
	
	public void initialize() {
		
		serverPortField.setText(NetworkDefaults.PORT);
		serverPortField.setDisable(true);
		portCheckbox.setSelected(true);
		connectButton.setDisable(true);
		
		addValidateListener(serverIPField);
		addValidateListener(serverPasswordField);
		addValidateListener(serverPortField);
		
		connectButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				connectButton.fire();
			}
		});
		
		connectButton.setOnAction(ae -> {			
			disableGUI(true);
			updateStatusLabel("Connecting to " + serverIPField.getText() + ":" + serverPortField.getText() + "...", "DarkOrange");
			
			if(!establishConnection(serverIPField.getText(), serverPortField.getText(), serverPasswordField.getText())) {
				TimedTask.runLater(Duration.millis(500), () -> {
					disableGUI(false);
					updateStatusLabel("Ready to connect to server...", "DarkGreen");
				});
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
		
		portCheckbox.selectedProperty().addListener((obs, newv, oldv) -> {
			if(newv == false) {
				port_field = serverPortField.getText();
				serverPortField.setText(NetworkDefaults.PORT);
				serverPortField.setDisable(true);
			}
			else {
				serverPortField.setText(port_field == null ? "" : port_field);
				serverPortField.setDisable(false);
			}
		});
		
		serverIPField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	String fieldText = serverIPField.getText();
		        if (!newValue.matches("[.0-9]")) {
		            fieldText = newValue.replaceAll("[^.0-9]", "");
		        }
		        if(newValue.length() > 15) {
		        	fieldText = newValue.substring(0, 15);
		        }
		        serverIPField.setText(fieldText);
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
		
		serverIPField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				connectButton.fire();
			}
		});
		
		serverPasswordField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				connectButton.fire();
			}
		});
		
		serverPortField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				connectButton.fire();
			}
		});
	}
	
	private boolean establishConnection(String ip_address, String port, String password) {
		
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
		
		Socket serverSocket;
		try {
			serverSocket = new Socket(ip_address, int_port);
		} catch (Exception e) {
			Alerter.exception("Failed to connect", "Could not connect with " + ip_address + ":" + int_port, e);
			return false;
		}
		
		ClientSocketHandler socketHandler = new ClientSocketHandler(serverSocket);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatClient.fxml"));
		ChatClientController controller = new ChatClientController(socketHandler);
		loader.setController(controller);
		
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Alerter.exception("Failed to connect", "Error occured while loading the ChatLobby UI", e);	
			return false;
		}
		Scene scene = new Scene(root);
		myStage.setScene(scene);
		
		return true;
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
		serverPortField.setDisable(disable);
		portCheckbox.setDisable(disable);
		connectButton.setDisable(disable);
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
