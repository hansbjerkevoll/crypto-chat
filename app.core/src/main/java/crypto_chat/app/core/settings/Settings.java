package crypto_chat.app.core.settings;

public class Settings {

	/*
	 * General settings
	 */
	private String user_name;
	private String history_location;
	
	/**
	 * Host Settings
	 */
	private String server_name;
	
	/**
	 * Join settings
	 */
	private String ip_address;
	
	public Settings(String user_name, String history_location, String server_name, String ip_address) {
		this.user_name = user_name;
		this.history_location = history_location;
		this.server_name = server_name;
		this.ip_address = ip_address;
	}
	
	public String getHistory_location() {
		return this.history_location;
	}
	
	public void setHistory_location(String history_location) {
		this.history_location = history_location;
	}
	
	public String getUser_name() {
		return user_name;
	}
	
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getServer_name() {
		return server_name;
	}
	
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
	
	public String getIp_address() {
		return ip_address;
	}
	
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	
}
