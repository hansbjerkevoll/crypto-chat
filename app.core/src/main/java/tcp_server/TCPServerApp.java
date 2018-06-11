package tcp_server;

import java.util.Iterator;

public class TCPServerApp {

	public static final int DEFAULT_PORT = 64672;
	private TCPServer server = null;


	public static void main(String[] args) {

		TCPServerApp app = new TCPServerApp();
		app.initializeServer(DEFAULT_PORT);
		app.startServer();

		// Shut down any client threads
		for (Iterator<Thread> iterator = Globals.CLIENT_THREADS.iterator(); iterator.hasNext();) {
			Thread thread = iterator.next();
			thread.interrupt();
		}
		long patienceInMs = 1000;
		long startWait = System.currentTimeMillis();
		try {
			for (Iterator<Thread> iterator = Globals.CLIENT_THREADS.iterator(); iterator.hasNext();) {
				Thread thread = iterator.next();
				long timeToWait = Math.max(1, startWait + patienceInMs - System.currentTimeMillis());
				thread.join(timeToWait);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void initializeServer(int port) {
		server = new TCPServer(port);
	}

	public void initializeServer() {
		server = new TCPServer(DEFAULT_PORT);
	}

	public void startServer() {
		server.start();
	}

	public void disconnectServer() {
		server.disconnect();
	}

	public Integer getServerPort() {
		if (server == null) {
			return null;
		}
		return server.getAssingedPort();
	}

	
}
