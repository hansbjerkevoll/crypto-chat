package crypto_chat.app.core.json_models;

public enum MessageType {
	// Connection
	WELCOME,
	CONNECTION_REQUEST,
	CONNECTION_RESPONSE,
	CLOSED,
	
	// Chat Messages
	TEXT_MESSAGE,
	IMAGE_MESSAGE,
	FILE_MESSAGE,
	MESSAGE_LOG
}
