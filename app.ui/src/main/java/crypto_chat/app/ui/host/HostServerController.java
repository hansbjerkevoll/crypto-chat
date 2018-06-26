package crypto_chat.app.ui.host;

import java.io.IOException;
import java.net.ServerSocket;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.globals.NetworkDefaults;
import crypto_chat.app.core.util.Alerter;

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
		
		ControllerFunctions.buttonActionEnter(hostButton);
		ControllerFunctions.buttonActionEnter(cancelButton);
		
		hostButton.setOnAction(ae -> {
			
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					disableGUI(true);
					updateMessage("Setting up server...");	
					statusLabel.setStyle("-fx-text-fill:DarkOrange");
					return null;
				}
				
			};
			
			statusLabel.textProperty().bind(task.messageProperty());
		
			new Thread(task).start();	
			
			task.setOnSucceeded(e -> {
				task.cancel();
				statusLabel.textProperty().unbind();
				
				ServerSocket serversocket = setupServer(serverPortField.getText());
				if(serversocket != null) {
					initializeLobbyUI(serversocket);
				}
				else {
					statusLabel.setText("Failed to host server...");
					statusLabel.setStyle("-fx-text-fill:Red");
					disableGUI(false);
				}
			});
			
			task.setOnFailed(e -> {
				task.cancel();
				statusLabel.textProperty().unbind();
				statusLabel.setText("Failed to host server...");
				statusLabel.setStyle("-fx-text-fill:Red");
				disableGUI(false);
				
			});
			
			
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
		
		cancelButton.setOnAction(ae -> {
			if (mainMenuScene != null) {
				((Stage) cancelButton.getScene().getWindow()).setScene(mainMenuScene);
			}
		});
		
		serverNameField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				hostButton.fire();
			}
		});
		
		serverPasswordField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				hostButton.fire();
			}
		});
		
		serverPortField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				hostButton.fire();
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
	
	private ServerSocket setupServer(String port) {
		int int_port = 0; 
		try {
			int_port = Integer.valueOf(port); 
		} catch (NumberFormatException e) {
			Alerter.error("Failed to host server", "Port must be an integer in the range 0-65535");
			return null;
		}
		if(int_port < 0 || int_port > 65535) {
			Alerter.error("Failed to host server", "Port must be an integer in the range 0-65535");
			return null;
		}
		
		ServerSocket serversocket;
		try {
			serversocket = new ServerSocket(int_port);
			serversocket.setSoTimeout(1000);
		}catch (IOException e) {
			Alerter.exception("Failed host server", "Error when creating socket", e);
			return null;
		}
			
		return serversocket;
	}
	
	private void initializeLobbyUI(ServerSocket serversocket) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatLobbyHost.fxml"));
		ChatLobbyHostController controller = new ChatLobbyHostController(myStage, serversocket, serverNameField.getText(), serverPasswordField.getText());
		loader.setController(controller);
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			Alerter.exception("Failed host server", "Error encountered while creating the lobby UI", e);
			return;
		}
		Scene s = new Scene(root);
		myStage.setScene(s);
		
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
		serverPortField.setDisable(portCheckbox.isSelected() ? true : disable);
		portCheckbox.setDisable(disable);
		hostButton.setDisable(disable);
		cancelButton.setDisable(disable);
	}
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}

}
