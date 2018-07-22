package crypto_chat.app.ui.admin.settings;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.Alerter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsController {
	
	private Stage myStage;
	private Scene adminMenuScene;
	
	private Settings current_settings;
	
	@FXML Button loadChatButton, saveButton, restoreButton, backButton;
	@FXML TextField userNameField, chatLocationField, serverNameField, ipField;
	
	private boolean changed_settings = false;
	
	public SettingsController(Stage stage) {
		this.myStage = stage;
	}
	
	public void initialize() {		
		
		saveButton.setDisable(true);
		
		current_settings = SettingsFactory.getSettings();
		
		if(current_settings != null) {
			userNameField.setText(current_settings.getUser_name());
			chatLocationField.setText(current_settings.getHistory_location() == null ? "" : current_settings.getHistory_location());
			serverNameField.setText(current_settings.getServer_name());
			ipField.setText(current_settings.getIp_address());
		}
		
		loadChatButton.setOnAction(ae -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Select default chat history directory");
			if(!chatLocationField.getText().equals("")) {
				chooser.setInitialDirectory(new File(chatLocationField.getText()));
			}			
			File selectedDirectory = chooser.showDialog(myStage);
			if(selectedDirectory != null) {
				chatLocationField.setText(selectedDirectory.getAbsolutePath());
			}
		});
		
		saveButton.setOnAction(ae -> {
			saveSettings();
		});
		
		restoreButton.setOnAction(ae -> {
			Optional<ButtonType> result = Alerter.confirmation("Restore settings?", "Are you sure you want to restore your settings to their default values? "
					+ "This will delete all changes you have made.");
			if(result.get() == ButtonType.OK) {
				userNameField.setText("");
				chatLocationField.setText("");
				serverNameField.setText("");
				ipField.setText("");
				saveSettings();
			}
		});
		
		backButton.setOnAction(ae -> {
			if(changed_settings) {
				ObservableList<ButtonType> buttons = FXCollections.observableArrayList();
				buttons.add(ButtonType.YES);
				buttons.add(ButtonType.NO);
				Optional<ButtonType> result = Alerter.confirmation("Save settings?", "The settings have been changed, do you want save the changes?", buttons);
				if(result.get() == ButtonType.YES) {
					saveSettings();
				} 
			}
			if (adminMenuScene != null) {
				myStage.setOnCloseRequest(null);
				((Stage) backButton.getScene().getWindow()).setScene(adminMenuScene);
			}
		});
		
		myStage.setOnCloseRequest(cr -> {
			cr.consume();
			if(changed_settings) {
				ObservableList<ButtonType> buttons = FXCollections.observableArrayList();
				buttons.add(ButtonType.YES);
				buttons.add(ButtonType.NO);
				Optional<ButtonType> result = Alerter.confirmation("Save settings?", "The settings have been changed, do you want save the changes?", buttons);
				if(result.get() == ButtonType.YES) {
					saveSettings();
				} 
			}
			myStage.close();			
		});
		
		ControllerFunctions.buttonActionEnter(saveButton);
		ControllerFunctions.buttonActionEnter(backButton);
		
		enableSave(userNameField);
		enableSave(chatLocationField);
		enableSave(serverNameField);
		enableSave(ipField);
		
	}
	
	private boolean saveSettings() {
		String user_name = userNameField.getText();
		String history_location = chatLocationField.getText();
		String server_name = serverNameField.getText();
		String ip_address = ipField.getText();
		
		Settings settings = new Settings(user_name, history_location, server_name, ip_address);
		
		try {
			SettingsFactory.saveSettingsLocalFile(settings);
			Alerter.info(null, "Settings have been saved");
			saveButton.setDisable(true);
			changed_settings = false;
			return true;
		} catch (IOException e) {
			Alerter.exception(null, "Failed to save settings...", e);
		}
		return false;
	}
	
	private void enableSave(TextField field) {
		field.textProperty().addListener((obs, oldv, newv) -> {
			if(!newv.equals(oldv)) {
				saveButton.setDisable(false);
				changed_settings = true;
			}
		});
	}
	
	public void setAdminMenuScene(Scene adminMenuScene) {
		this.adminMenuScene = adminMenuScene;
	}

}
