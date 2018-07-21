package crypto_chat.app.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChatHistory {

	
	public static void saveHistoryToLocalFile(ArrayList<String> chatHistory, String filePath) throws IOException {
		Path file = Paths.get(filePath);
		Files.write(file, chatHistory, Charset.forName("UTF-8"));
	}
	
	
	public static ArrayList<String> readHistoryFromFile(File file) throws IOException{		
		ArrayList<String> chatHistory = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String msg;
			while((msg = br.readLine()) != null) {
				chatHistory.add(msg);
			}
		}
		
		return chatHistory;
	}
	
}
