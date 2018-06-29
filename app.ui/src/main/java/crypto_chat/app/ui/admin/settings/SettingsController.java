package crypto_chat.app.ui.admin.settings;

import java.io.IOException;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.core.settings.Settings;
import crypto_chat.app.core.settings.SettingsFactory;
import crypto_chat.app.core.util.Alerter;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {
	
	private Scene adminMenuScene;
	
	private Settings current_settings;
	
	@FXML Button saveButton, backButton;
	@FXML TextField hostNameField, serverNameField, serverPasswordField, clientNameField, ipField, joinPasswordField;
	
	public void initialize() {		
		
		saveButton.setDisable(true);
		
		current_settings = SettingsFactory.getSettings();
		
		if(current_settings != null) {
			hostNameField.setText(current_settings.getHost_name());
			serverNameField.setText(current_settings.getServer_name());
			serverPasswordField.setText(current_settings.getServer_password());
			clientNameField.setText(current_settings.getClient_name());
			ipField.setText(current_settings.getIp_address());
			joinPasswordField.setText(current_settings.getJoin_password());
		}
		
		saveButton.setOnAction(ae -> {
			String host_name = hostNameField.getText();
			String server_name = serverNameField.getText();
			String server_password = serverPasswordField.getText();
			String client_name = clientNameField.getText();
			String ip_address = ipField.getText();
			String join_password = joinPasswordField.getText();
			
			Settings settings = new Settings(host_name, server_name, server_password, client_name, ip_address, join_password);
			
			try {
				SettingsFactory.saveSettingsLocalFile(settings);
			} catch (IOException e) {
				Alerter.exception(null, "Failed to save settings...", e);
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
		enableSave(serverPasswordField);
		enableSave(clientNameField);
		enableSave(ipField);
		enableSave(joinPasswordField);
		
	}
	
	private void enableSave(TextField field) {
		field.textProperty().addListener((obs, oldv, newv) -> {
			if(!oldv.equals(newv)) {
				saveButton.setDisable(false);
			}
		});
	}
	
	public void setAdminMenuScene(Scene adminMenuScene) {
		this.adminMenuScene = adminMenuScene;
	}

}
