package crypto_chat.app.ui.client;

import crypto_chat.app.ui.globals.NetworkDefaults;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class JoinServerController {

	Stage myStage;
	Scene mainMenuScene;
	
	@FXML Button cancelButton, hostButton;
	@FXML CheckBox portCheckbox;
	@FXML TextField serverIPField, serverPassword, serverPortField;
	
	public JoinServerController(Stage stage) {
		myStage = stage;
	}
	
	public void initialize() {
		
		serverPortField.setText(NetworkDefaults.PORT);
		serverPortField.setDisable(true);
		portCheckbox.setSelected(true);
		
		portCheckbox.selectedProperty().addListener((obs, newv, oldv) -> {
			if(newv == false) {
				serverPortField.setText(Integer.toString(27567));
				serverPortField.setDisable(true);
			}
			else {
				serverPortField.setText("");
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
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}
}
