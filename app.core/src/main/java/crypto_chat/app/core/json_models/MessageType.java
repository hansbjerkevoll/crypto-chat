package crypto_chat.app.core.json_models;

public enum MessageType {
	// Connection
	WELCOME,
	CONNECTION_REQUEST,
	CONNECTION_RESPONSE,
	CONNECTION_CLOSED,
	
	// Chat Messages
	TEXT_MESSAGE,
	IMAGE_MESSAGE,
	FILE_MESSAGE
}
