package crypto_chat.app.ui.admin;

import java.io.IOException;
import java.net.URL;

import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.ui.admin.settings.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMenuController {
	
	Stage myStage;
	
	Scene mainMenuScene;
	Scene settingsScene;
	
	@FXML Button settingsButton, portforwardButton, backButton;
	
	public AdminMenuController(Stage stage) {
		this.myStage = stage; 
	}
	
	@FXML public void initialize() {
		
		ControllerFunctions.buttonActionEnter(settingsButton);
		ControllerFunctions.buttonActionEnter(portforwardButton);
		ControllerFunctions.buttonActionEnter(backButton);
		
		settingsButton.setOnAction(ae -> {
			try {
				if (settingsScene == null) {
					URL location = getClass().getResource("settings/Settings.fxml");
					FXMLLoader loader = new FXMLLoader(location);
					SettingsController controller = new SettingsController(myStage);
					controller.setAdminMenuScene(settingsButton.getScene());
					loader.setController(controller);
					Parent root = loader.load();
					this.settingsScene = new Scene(root);
				}
				myStage.setScene(settingsScene);
			} catch (IOException e) {
				e.printStackTrace();
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
