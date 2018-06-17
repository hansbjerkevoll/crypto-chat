package crypto_chat.app.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import crypto_chat.app.ui.admin.AdminMenuController;
import crypto_chat.app.ui.client.JoinServerController;
import crypto_chat.app.ui.globals.ResourceLocations;
import crypto_chat.app.ui.host.HostServerController;

public class MainMenuController {
	
	Stage myStage;
	Scene clientScene = null;
	Scene serverScene = null;
	Scene adminScene = null;
	
	@FXML Button joinButton, hostButton, adminButton;
	
	public MainMenuController(Stage stage) {
		this.myStage = stage;
	}
	
	@FXML public void initialize(){
		
		hostButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				hostButton.fire();
			}
		});
		
		hostButton.setOnAction(ae -> {			
			try {
				if (serverScene == null) {
					URL location = getClass().getResource(ResourceLocations.FXML_HOST_SERVER);
					FXMLLoader loader = new FXMLLoader(location);
					HostServerController controller = new HostServerController(myStage);
					controller.setMainMenuScene(hostButton.getScene());
					loader.setController(controller);
					Parent root = loader.load();
					this.serverScene = new Scene(root);
				}
				myStage.setScene(serverScene);
			} catch (IOException e) {
				e.printStackTrace();
		
			}					
		});
		
		joinButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				joinButton.fire();
			}
		});
		
		joinButton.setOnAction(ae -> {
			try {
				if (clientScene == null) {
					URL location = getClass().getResource(ResourceLocations.FXML_JOIN_SERVER);
					FXMLLoader loader = new FXMLLoader(location);
					JoinServerController controller = new JoinServerController(myStage);
					controller.setMainMenuScene(joinButton.getScene());
					loader.setController(controller);
					Parent root = loader.load();
					this.clientScene = new Scene(root);
				}
				myStage.setScene(clientScene);
			} catch (IOException e) {
				e.printStackTrace();
		
			}
		});
		
		adminButton.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				adminButton.fire();
			}
		});
		
		adminButton.setOnAction(ae -> {
			try {
				if (adminScene == null) {
					URL location = getClass().getResource(ResourceLocations.FXML_ADMIN_MENU);
					FXMLLoader loader = new FXMLLoader(location);
					AdminMenuController controller = new AdminMenuController(myStage);
					controller.setMainMenuScene(adminButton.getScene());
					loader.setController(controller);
					Parent root = loader.load();
					this.adminScene = new Scene(root);
				}
				myStage.setScene(adminScene);
			} catch (IOException e) {
				e.printStackTrace();
		
			}
		});
		
	}

}
