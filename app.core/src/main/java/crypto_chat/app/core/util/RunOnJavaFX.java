package crypto_chat.app.core.util;

import javafx.application.Platform;

public class RunOnJavaFX {
	public static void run(Runnable r) {
		if (Platform.isFxApplicationThread())
			r.run();
		else
			Platform.runLater(r);
	}
}
