package crypto_chat.app.core.settings;

public class Settings {

	
	/**
	 * Host Settings
	 */
	private String host_name;
	private String server_name;
	private String server_password;
	
	/**
	 * Join settings
	 */
	private String client_name;
	private String ip_address;
	private String join_password;
	
	public Settings(String host_name, String server_name, String server_password, String join_name, String ip_address, String join_password) {
		this.host_name = host_name;
		this.server_name = server_name;
		this.server_password = server_password;
		this.client_name = join_name;
		this.ip_address = ip_address;
		this.join_password = join_password;
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
	
	public String getServer_password() {
		return server_password;
	}
	
	public void setServer_password(String server_password) {
		this.server_password = server_password;
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
	
	public String getJoin_password() {
		return join_password;
	}
	
	public void setJoin_password(String join_password) {
		this.join_password = join_password;
	}
	
}
