package crypto_chat.app.ui.admin;

import crypto_chat.app.core.globals.ControllerFunctions;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMenuController {
	
	Stage myStage;
	
	Scene mainMenuScene;
	
	@FXML Button settingsButton;
	@FXML Button portforwardButton;
	@FXML Button backButton;;
	
	public AdminMenuController(Stage stage) {
		this.myStage = stage; 
	}
	
	@FXML public void initialize() {
		
		ControllerFunctions.buttonActionEnter(settingsButton);
		ControllerFunctions.buttonActionEnter(portforwardButton);
		ControllerFunctions.buttonActionEnter(backButton);
		
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
