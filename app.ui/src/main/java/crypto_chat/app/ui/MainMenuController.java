package crypto_chat.app.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import crypto_chat.app.core.globals.ControllerFunctions;
import crypto_chat.app.ui.admin.AdminMenuController;
import crypto_chat.app.ui.client.JoinServerController;
import crypto_chat.app.ui.host.HostServerController;

public class MainMenuController {
	
	Stage myStage;
	Scene adminScene = null;
	
	@FXML Button joinButton, hostButton, adminButton;
	
	public MainMenuController(Stage stage) {
		this.myStage = stage;
	}
	
	public void initialize(){
		
		hostButton.setOnAction(ae -> {			
			try {
				URL location = getClass().getResource("host/HostServer.fxml");
				FXMLLoader loader = new FXMLLoader(location);
				HostServerController controller = new HostServerController(myStage);
				controller.setMainMenuScene(hostButton.getScene());
				loader.setController(controller);
				Parent root = loader.load();
				Scene hostscene = new Scene(root);
				myStage.setScene(hostscene);
			} catch (IOException e) {
				e.printStackTrace();
			}					
		});
		
		joinButton.setOnAction(ae -> {
			try {
				URL location = getClass().getResource("client/JoinServer.fxml");
				FXMLLoader loader = new FXMLLoader(location);
				JoinServerController controller = new JoinServerController(myStage);
				controller.setMainMenuScene(joinButton.getScene());
				loader.setController(controller);
				Parent root = loader.load();
				Scene joinscene = new Scene(root);
				myStage.setScene(joinscene);
			} catch (IOException e) {
				e.printStackTrace();
		
			}
		});
		
		adminButton.setOnAction(ae -> {
			try {
				if (adminScene == null) {
					URL location = getClass().getResource("admin/AdminMenu.fxml");
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
		
		ControllerFunctions.buttonActionEnter(joinButton);
		ControllerFunctions.buttonActionEnter(hostButton);
		ControllerFunctions.buttonActionEnter(adminButton);
		
	}

}
