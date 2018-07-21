package crypto_chat.app.core.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsFactory {
	
	private static final String APP_FOLDER_NAME = ".crypto-chat";
	private static final String SETTINGS_NAME = "settings.properties";
	
	/*
	 * General Settings
	 */
	private static final String KEY_HISTORY_LOCATION = "history_location";
	
	/**
	 * Host Settings
	 */
	private static final String KEY_HOST_NAME = "host_name";
	private static final String KEY_SERVER_NAME = "server_name";
	
	/**
	 * Join settings
	 */
	
	private static final String KEY_CLIENT_NAME = "client_name";
	private static final String KEY_IP_ADDRESS = "ip_address";
	
	
	private static Settings settings = null;
	private static File settingsFile = null;

	public static Settings getSettings() {
		if (settings == null) {
			File settingsFile = getSettingsFile();
			if (!settingsFile.exists()) {
				return null;
			}
			Properties p = new Properties();
			try (InputStream is = new FileInputStream(settingsFile)) {
				p.load(is);
				String history_location = p.getProperty(KEY_HISTORY_LOCATION);
				String host_name = p.getProperty(KEY_HOST_NAME);
				String server_name = p.getProperty(KEY_SERVER_NAME);
				String client_name = p.getProperty(KEY_CLIENT_NAME);
				String ip_address = p.getProperty(KEY_IP_ADDRESS);
				settings = new Settings(history_location, host_name, server_name,  client_name, ip_address);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return settings;
	}
	
	public static void saveSettings(Settings new_settings) {
		settings = new_settings;
	}
	
	public static void saveSettingsLocalFile(Settings settings) throws IOException {
		File logincredFile = getSettingsFile();
		File parent = logincredFile.getParentFile(); 
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Couldn't create directory");
			}
		}
		
		try (FileOutputStream fos = new FileOutputStream(logincredFile)){
			Properties p = new Properties();
			p.setProperty(KEY_HISTORY_LOCATION, settings.getHistory_location());
			p.setProperty(KEY_HOST_NAME, settings.getHost_name());
			p.setProperty(KEY_SERVER_NAME, settings.getServer_name());
			p.setProperty(KEY_CLIENT_NAME, settings.getClient_name());
			p.setProperty(KEY_IP_ADDRESS, settings.getIp_address());
			
			p.store(fos, " Crypto Chat Settings:");
		}
		
		saveSettings(settings);
	}
	
	public static void deleteFromDisk() {
		File settingsFile = getSettingsFile();
		if (settingsFile.exists() && !settingsFile.isDirectory()) {
			settingsFile.delete();
		}
	}
	
	public static boolean fileExists() {
		File settingsFile = getSettingsFile();
		return settingsFile.exists() && !settingsFile.isDirectory();
	}
	
	private static File getSettingsFile() {
		if (settingsFile == null) {
			String userhome = System.getProperty("user.home");
			Path appFolder = Paths.get(userhome, APP_FOLDER_NAME);
			settingsFile = appFolder.resolve(SETTINGS_NAME).toFile();
		}
		return settingsFile;
	}
	
	public static void setSettingsFile(File newFile) {
		settingsFile = newFile;
	}

}
