package crypto_chat.app.ui.server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Queue;

import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.ui.host.ChatHostController;

public class ClientThread implements Runnable {
	
	private ChatHostController controller;
	private ObservableClient observableClient;
	private Socket clientSocket;
	private String name = "N/A";
	private Queue<String> messageToClient = new ArrayDeque<>();
	private Queue<String> messageFromClient = new ArrayDeque<>();
	private Boolean disconnected = false;
	
	private PrintStream toClient;
	private BufferedReader fromClient;
	
	public ClientThread(ChatHostController controller, Socket clientSocket) {
		this.controller = controller;
		this.clientSocket = clientSocket;
	}
	
	public void setObservableClient(ObservableClient client) {
		synchronized (this) {
			this.observableClient = client;
		}
	}
	
	public ObservableClient getObservableClient() {
		return this.observableClient;
	}

	@Override
	public void run() {
		try {
			this.toClient = new PrintStream(clientSocket.getOutputStream(), true, "UTF-8");
			this.fromClient = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), Charset.forName("UTF-8")));
			synchronized (this) {
				while (!(Thread.currentThread().isInterrupted() || toClient.checkError())) {
					this.wait(400);
					// Write to client
					while (messageToClient.size() > 0) {
						String msg = messageToClient.poll();
						toClient.println(msg);
					}
					// Read from client
					String readLine;
					while (fromClient.ready() && (readLine = fromClient.readLine()) != null) {
						this.messageFromClient.offer(readLine);
					}
					// Tell controller if we received message
					if (messageFromClient.size() > 0) {
						controller.gotMessageFromClient(this);
					}
					// Disconnect if disconnected
					if(disconnected) {
						Thread.currentThread().interrupt();
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println(name + " was interrupted, exiting.");
		} catch (IOException e) {
			System.err.println(this.name + " got an exception.");
			e.printStackTrace();
		}
		
		System.out.printf("Closing thread \"%s\"\n", name);
		closeResources();
		controller.removeClient(observableClient);
		Threads.THREADS.remove(Thread.currentThread());
		
	}
	
	private void closeHelper(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
			}
		}
	}

	private void closeResources() {
		closeHelper(toClient);
		closeHelper(fromClient);
		closeHelper(clientSocket);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIP() {
		return clientSocket.getInetAddress().getHostAddress();
	}

	public void sendMessageToClient(String msg) {
		synchronized (this) {
			messageToClient.offer(msg);
			this.notifyAll();
		}
	}

	public String getMessage() {
		synchronized (this) {
			return messageFromClient.poll();
		}
	}
	
	public void disconnectClient() {
		disconnected = true;
	}

}
