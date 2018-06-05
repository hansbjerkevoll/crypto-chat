package tcp_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class TCPClient implements Runnable {

	private Socket socket;

	public TCPClient(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (Socket cs = socket;
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				PrintStream out = new PrintStream(socket.getOutputStream(), true, "UTF-8")) {
			System.out.printf("%s connected\n", getReadableSocket(cs));
		
			
			String client_message = inFromClient.readLine();	
			
			out.println(client_message);
						
			System.out.printf("Disconnecting %s\n", getReadableSocket(cs));
		} catch (IOException e) {
			// TODO: Parse if connection was closed?
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Globals.CLIENT_THREADS.remove(Thread.currentThread());
		}
	}

	private String getReadableSocket(Socket s) {
		return String.format("%s (%s:%d)", s.getInetAddress().getHostName(), s.getInetAddress().getHostAddress(),
				s.getPort());
	}
}
