import java.net.*;
import java.lang.*;
import java.io.*;

public class ConnectionService extends Thread {
	private final int MAX_REQUEST_LENGTH = 16;

	private int id;
	private Server server;
	private Socket socket;
	private InetAddress address;

	private BufferedReader reader;
	private PrintWriter writer;

	public ConnectionService (Socket socket, InetAddress address, Server server, int id)
	{
		this.socket = socket;
		this.server = server;
		this.address = address;
		this.id = id;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
 			e.printStackTrace();
 		}
	}

	private void processRequest(String request)
	{
		// Close connection.
		if (request.equals("CLOSE")) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
			}
			catch (IOException e) {
 				e.printStackTrace();
 			}
		}
		else if (request.equals("SETUP")) {
			sendMessage("id:" + Integer.toString(id));
		}
	}

	private void sendMessage(String message)
	{
		if (writer == null)
			return;
		
		writer.println(message);
		System.out.println("Send: " + message);
	}

	@Override
	public void run()
	{
		while (true) {
			String request = null;
			try {
				request = reader.readLine();

				if (request == null)
					break;

				System.out.println("Received: " + request);
				processRequest(request);
			}
			catch (IOException e) {
				e.printStackTrace();
				break;
	 		}
		}

		try {
			reader.close();
		 	reader = null;

		 	writer.close();
			writer = null;

			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		server.removeClient(address, id);
 		System.out.println("Disconnect " + socket.getInetAddress());
	}
}