package crypto_chat.app.core.globals;

import java.util.Date;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ChatFunctions {
	
	public static void newTextMessage(VBox chatRoom, String header, String message, long timestamp) {		
		Date msg_time = new Date(timestamp);
		VBox messageBox = new VBox();
		Label headerLabel = new Label(header + " [" + msg_time + "]");
		Label messageLabel = new Label(message);
		messageBox.setMaxWidth(775);
		messageBox.setPadding(new Insets(2));
		headerLabel.setWrapText(true);
		messageLabel.setWrapText(true);
		headerLabel.setStyle("-fx-font-weight:Bold");
		messageBox.getChildren().addAll(headerLabel, messageLabel);
		chatRoom.getChildren().add(messageBox);
			
	}
	
	

}
