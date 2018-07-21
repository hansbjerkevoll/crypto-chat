package crypto_chat.app.ui.server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Queue;

import crypto_chat.app.ui.client.ChatClientController;

public class ClientSocketHandler implements Runnable {
	
	private Socket serverSocket;
	private ChatClientController controller;
	
	private BufferedReader fromServer;
	private PrintStream toServer;
	private final Queue<String> messageToServer = new ArrayDeque<>();
	private final Queue<String> messageFromServer = new ArrayDeque<>();
	private Boolean disconnected = false;
	
	public ClientSocketHandler(Socket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	@Override
	public void run() {
		// Open streams to server
		try {
			toServer = new PrintStream(serverSocket.getOutputStream(), true, "UTF-8");
			fromServer = new BufferedReader(
					new InputStreamReader(serverSocket.getInputStream(), Charset.forName("UTF-8")));
			while (!Thread.currentThread().isInterrupted()) {
				synchronized (this) {
					this.wait(100);
					// Read from socket
					String line;
					while (fromServer.ready() && (line = fromServer.readLine()) != null) {
						if (!messageFromServer.offer(line)) {
							System.err.println("Error: Received message from server but could not add it to the queue\n"
									+ "Message was:\n" + line);
						}
					}
					if (!messageFromServer.isEmpty()) {
						controller.gotMessageFromServer();
					}
					// Write to socket
					while ((line = messageToServer.poll()) != null) {
						System.out.println(line);
						toServer.println(line);
					}
					if(disconnected) {
						Thread.currentThread().interrupt();
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Client thread was interrupted, exiting");
		} catch (IOException e) {
			System.err.println("Error on socket to server");
			e.printStackTrace();
		}
		
		stopStreams();
	}
	
	public boolean sendMessageToServer(String message) {
		boolean didSend = false;
		synchronized (this) {
			didSend = messageToServer.offer(message);
			this.notifyAll();
		}
		return didSend;
	}

	public boolean hasMessage() {
		synchronized (this) {
			return messageFromServer.size() > 0;
		}
	}

	public String getMessage() {
		synchronized (this) {
			return messageFromServer.poll();
		}
	}
	
	private void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
			}
		}
	}

	private void stopStreams() {
		close(fromServer);
		close(toServer);
		close(serverSocket);
	}
	
	public InetAddress getIP() {
		return serverSocket.getInetAddress();
	}
	
	public int getPort() {
		return serverSocket.getLocalPort();
	}
	
	public void setController(ChatClientController controller) {
		this.controller = controller;
	}
	
	public void disconnect() {
		disconnected = true;
	}
	
}
