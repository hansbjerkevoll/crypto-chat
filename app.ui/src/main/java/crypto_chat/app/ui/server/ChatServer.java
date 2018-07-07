package crypto_chat.app.ui.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crypto_chat.app.core.globals.Threads;
import crypto_chat.app.ui.host.ChatHostController;

public class ChatServer implements Runnable{
	
	private final List<ClientThread> clients = new ArrayList<>();
	
	ChatHostController controller;
	String serverName, serverPassword;
	ServerSocket serverSocket;
	
	public ChatServer(ChatHostController controller, ServerSocket serverSocket, String serverName, String serverPassword) {
		this.controller = controller;
		this.serverSocket = serverSocket;
		this.serverName = serverName;
		this.serverPassword = serverPassword;
	}
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Socket clientSocket = serverSocket.accept(); 
				ClientThread clientThread = new ClientThread(controller, clientSocket);
				Thread t = new Thread(clientThread);
				t.start();
				Threads.THREADS.add(t);
				addClient(clientThread);
			} catch (SocketTimeoutException e) {
				// Timeout, loop around, check if thread is interrupted
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
		closeSockets();
		
	}
	
	private void closeSockets() {
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	private void addClient(ClientThread c) {
		synchronized (this) {
			this.clients.add(c);
		}
	}
	
	public boolean removeClient(ClientThread c) {
		synchronized (this) {
			return this.clients.remove(c);
		}
	}

	public Iterator<ClientThread> getClients() {
		synchronized (this) {
			return clients.iterator();
		}
	}

}
