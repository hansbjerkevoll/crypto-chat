package crypto_chat.app.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AppController {
	
	@FXML Button joinServerButton;
	
	public void initialize(){
		
		joinServerButton.setOnAction(ae -> {
			try {
				Parent parent = FXMLLoader.load(getClass().getResource("host_server/HostServer.fxml"));
				Stage stage = new Stage();
				Scene scene = new Scene(parent);
				stage.setScene(scene);
				stage.setTitle("Legg til ny film");
				stage.initOwner(joinServerButton.getScene().getWindow());
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
	}

}
