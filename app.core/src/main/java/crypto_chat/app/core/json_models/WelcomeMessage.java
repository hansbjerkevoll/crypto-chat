package crypto_chat.app.core.json_models;

/**
 * Sent from server to client once client is connected
 */
public class WelcomeMessage extends JsonPackage {
	private String serverName;
	private String passwordHash;

	public WelcomeMessage(String serverName, String passwordHash) {
		messageType = MessageType.WELCOME;
		this.serverName = serverName;
		this.passwordHash = passwordHash;
	}

	public String getServerName() {
		return serverName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
}
