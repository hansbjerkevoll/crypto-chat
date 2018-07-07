package crypto_chat.app.ui.admin.settings;

import java.io.IOException;
import java.util.Optional;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.Alerter;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {
	
	private Scene adminMenuScene;
	
	private Settings current_settings;
	
	@FXML Button saveButton, restoreButton, backButton;
	@FXML TextField hostNameField, serverNameField, clientNameField, ipField;
	
	public void initialize() {		
		
		saveButton.setDisable(true);
		
		current_settings = SettingsFactory.getSettings();
		
		if(current_settings != null) {
			hostNameField.setText(current_settings.getHost_name());
			serverNameField.setText(current_settings.getServer_name());
			clientNameField.setText(current_settings.getClient_name());
			ipField.setText(current_settings.getIp_address());
		}
		
		saveButton.setOnAction(ae -> {
			String host_name = hostNameField.getText();
			String server_name = serverNameField.getText();
			String client_name = clientNameField.getText();
			String ip_address = ipField.getText();
			
			Settings settings = new Settings(host_name, server_name, client_name, ip_address);
			
			try {
				SettingsFactory.saveSettingsLocalFile(settings);
				Alerter.info(null, "Settings have been saved");
				saveButton.setDisable(true);
			} catch (IOException e) {
				Alerter.exception(null, "Failed to save settings...", e);
			}
		});
		
		restoreButton.setOnAction(ae -> {
			Optional<ButtonType> result = Alerter.confirmation("Restore settings?", "Are you sure you want to restore your settings to their default values?"
					+ "This will delete all changes you have made.");
			if(result.get() == ButtonType.OK) {
				hostNameField.setText("");
				serverNameField.setText("");
				clientNameField.setText("");
				ipField.setText("");
				try {
					SettingsFactory.saveSettingsLocalFile(new Settings("", "", "", ""));
				} catch (IOException e) {
					Alerter.exception(null, "Failed to save settings...", e);
				}
			}
		});
		
		backButton.setOnAction(ae -> {
			if (adminMenuScene != null) {
				((Stage) backButton.getScene().getWindow()).setScene(adminMenuScene);
			}
		});
		
		ControllerFunctions.buttonActionEnter(saveButton);
		ControllerFunctions.buttonActionEnter(backButton);
		
		enableSave(hostNameField);
		enableSave(serverNameField);
		enableSave(clientNameField);
		enableSave(ipField);
		
	}
	
	private void enableSave(TextField field) {
		field.textProperty().addListener((obs, oldv, newv) -> {
			if(!newv.equals(oldv)) {
				saveButton.setDisable(false);
			}
		});
	}
	
	public void setAdminMenuScene(Scene adminMenuScene) {
		this.adminMenuScene = adminMenuScene;
	}

}
