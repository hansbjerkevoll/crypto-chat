package crypto_chat.app.ui.client;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class JoinServerController {

	Stage myStage;

	Scene mainMenuScene;
	
	@FXML Button cancelButton;
	
	public JoinServerController(Stage stage) {
		myStage = stage;
	}
	
	public void initialize() {
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
