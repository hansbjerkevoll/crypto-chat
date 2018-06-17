package crypto_chat.app.ui.admin;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class AdminMenuController {
	
	Stage myStage;
	
	Scene mainMenuScene;
	
	@FXML Button backButton;
	
	public AdminMenuController(Stage stage) {
		this.myStage = stage; 
	}
	
	@FXML public void initialize() {
		
		backButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				backButton.fire();
			}
		});
		
		backButton.setOnAction(ae -> {
			if (mainMenuScene != null) {
				((Stage) backButton.getScene().getWindow()).setScene(mainMenuScene);
			}
		});
	}
	
	public void setMainMenuScene(Scene scene) {
		this.mainMenuScene = scene;
	}

}
