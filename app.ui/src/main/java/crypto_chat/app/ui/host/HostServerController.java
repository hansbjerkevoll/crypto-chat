package crypto_chat.app.ui.host;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.globals.NetworkDefaults;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.Alerter;
import crypto_chat.app.core.util.ChatHistory;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class HostServerController {
	
	private Stage myStage;
	private Scene mainMenuScene;
	private Settings settings;
	
	@FXML Button loadChatButton, cancelButton, hostButton;
	@FXML Label statusLabel, chatFileLabel;
	@FXML CheckBox portCheckbox;
	@FXML TextField hostNameField, serverNameField, serverPasswordField, serverPortField;
	
	private String directoryPath;
	private ArrayList<String> chatHistoryList;
	
	public HostServerController(Stage stage) {
		myStage = stage;
	}
	
	@FXML public void initialize() {
		
		settings = SettingsFactory.getSettings();
		
		if(settings != null) {
			loadSettings();
		}
				
		serverPortField.setText(NetworkDefaults.PORT);
		serverPortField.setDisable(true);
		portCheckbox.setSelected(true);
		hostButton.setDisable(true);
		
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
		
		cancelButton.setOnAction(ae -> {
			if (mainMenuScene != null) {
				((Stage) cancelButton.getScene().getWindow()).setScene(mainMenuScene);
			}
		});	
		
		portCheckbox.selectedProperty().addListener((obs, newv, oldv) -> {
			if(!newv) {
				serverPortField.setText(NetworkDefaults.PORT);
				serverPortField.setDisable(true);
			}
			else {
				serverPortField.clear();;
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
		
		loadChatButton.setOnAction(ae -> {
			try {
				selectChatFile();
			} catch (IOException e) {
				Alerter.exception("Loading failed", "Failed to load chat history", e);
			}
			
		});
		
		addValidateListener(serverNameField);
		addValidateListener(serverPasswordField);
		addValidateListener(serverPortField);
		
		ControllerFunctions.buttonActionEnter(hostButton);
		ControllerFunctions.buttonActionEnter(cancelButton);

		ControllerFunctions.fieldFireButton(serverNameField, hostButton);
		ControllerFunctions.fieldFireButton(serverPasswordField, hostButton);
		ControllerFunctions.fieldFireButton(serverPortField, hostButton);
		
		validateFields();
	
	}
	
	private void selectChatFile() throws IOException {
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Open Resource File");
		filechooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Chat files", "*.txt"));
		if(directoryPath != null) {
			filechooser.setInitialDirectory(new File(directoryPath));
		}

		File selectedFile = filechooser.showOpenDialog(myStage);
		if (selectedFile != null) {
			directoryPath = selectedFile.getParent();
			chatFileLabel.setText(selectedFile.getName());
			chatHistoryList = ChatHistory.readHistoryFromFile(selectedFile);
		}
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatHost.fxml"));
		ChatHostController controller = new ChatHostController(myStage, serversocket, hostNameField.getText(), serverNameField.getText(), serverPasswordField.getText());
		//System.out.println(chatHistoryList.size());
		if(chatHistoryList != null) controller.setChatHistory(chatHistoryList);	
		controller.setMainMenuScene(mainMenuScene);
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
		hostNameField.setDisable(disable);
		serverNameField.setDisable(disable);
		serverPasswordField.setDisable(disable);
		serverPortField.setDisable(portCheckbox.isSelected() ? true : disable);
		portCheckbox.setDisable(disable);
		hostButton.setDisable(disable);
		cancelButton.setDisable(disable);
	}
	
	private void loadSettings() {
		String saveName = settings.getUser_name();
		String saveServerName = settings.getServer_name();
		String historyLocation = settings.getHistory_location();
		serverNameField.setText(saveServerName);
		hostNameField.setText(saveName);
		directoryPath = historyLocation == null || "".equals(historyLocation) ? null : historyLocation;
		
		if("".equals(saveName) || saveName == null) {
			Platform.runLater(() -> {
				hostNameField.requestFocus();
			});
		} else if("".equals(saveServerName) || saveServerName == null) {
			Platform.runLater(() -> {
				serverNameField.requestFocus();
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
