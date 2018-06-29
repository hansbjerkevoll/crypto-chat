package crypto_chat.app.core.globals;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ControllerFunctions {
	
	public static void buttonActionEnter(Button button) {
		button.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				ke.consume();
				button.fire();
			}
		});
	}
	
	public static void fieldFireButton(TextField field, Button button) {
		field.setOnAction(ae -> {
			button.fire();
		});
	}
	
	public static void stopServer() throws InterruptedException {
		long patience = 1000;
		for (Thread t : Threads.THREADS) {
			t.interrupt();
		}
		long startWait = System.currentTimeMillis();
		for (Thread t : Threads.THREADS) {
			long timeToWait = Math.max(0, startWait - System.currentTimeMillis() + patience);
			t.join(timeToWait);
		}
	}

}
