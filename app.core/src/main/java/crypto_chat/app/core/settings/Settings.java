package crypto_chat.app.core.settings;

public class Settings {

	
	/**
	 * Host Settings
	 */
	private String host_name;
	private String server_name;
	
	/**
	 * Join settings
	 */
	private String client_name;
	private String ip_address;
	
	public Settings(String host_name, String server_name, String join_name, String ip_address) {
		this.host_name = host_name;
		this.server_name = server_name;
		this.client_name = join_name;
		this.ip_address = ip_address;
	}
	
	public String getHost_name() {
		return host_name;
	}
	
	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}
	
	public String getServer_name() {
		return server_name;
	}
	
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
	
	public String getClient_name() {
		return client_name;
	}
	
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	
	public String getIp_address() {
		return ip_address;
	}
	
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	
}
