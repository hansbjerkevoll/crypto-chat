package crypto_chat.app.ui;

import crypto_chat.app.core.globals.ControllerFunctions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
		MainMenuController controller = new MainMenuController(primaryStage);
		loader.setController(controller);
		Parent root = loader.load();
		Scene s = new Scene(root);
		primaryStage.setTitle("Crypto Chat");
		primaryStage.setResizable(false);
		primaryStage.setScene(s);
		primaryStage.setWidth(950);
		primaryStage.setHeight(750);
		primaryStage.show();
		System.out.println(primaryStage.getWidth());
		System.out.println(primaryStage.getHeight());
	}
	
	@Override
	public void stop() throws Exception {
		System.out.println("Interrupting all threads");
		ControllerFunctions.stopServer();
		System.out.println("Exiting...");
	}

}