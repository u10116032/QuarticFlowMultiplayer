import java.net.*;
import java.lang.*;
import java.io.*;

public class ConnectionService extends Thread {
	private final int MAX_REQUEST_LENGTH = 16;

	private int id;
	private Server server;
	private Socket socket;

	private BufferedReader reader;
	private PrintWriter writer;

	public ConnectionService (Socket socket, Server server) throws IOException
	{
		this.socket = socket;
		socket.setSoTimeout(1000);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
		
		this.server = server;
		id = -1;
		
 		QFLogger.INSTANCE.Log("Establish connection with " + socket.getInetAddress());
	}

	private void processRequest(String request)
	{
		if (request.equals("ALIVE"))
			return;
		else if (request.equals("ISALIVE")){
			sendMessage("ALIVE");
			return;
		}
		else if (request.equals("CLOSE")) {
			// Close connection.
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
			}
			catch (IOException e) {
 				e.printStackTrace();
 			}
		}
		else if (request.equals("SETUP")) {
			id = server.getId();
			sendMessage("id:" + Integer.toString(id));
		}

		QFLogger.INSTANCE.Log("Received: " + request);
	}

	private void sendMessage(String message)
	{
		if (writer == null)
			return;
		
		writer.println(message);

		if (message.equals("ALIVE") || message.equals("ISALIVE"))
			return;

		QFLogger.INSTANCE.Log("Send: " + message);
	}

	@Override
	public void run()
	{
		boolean isAlive = true;

		while (true) {
			String request = null;
			try {
				request = reader.readLine();
				
				if (request == null)
					break;

				isAlive = true;
				processRequest(request);
			}
			catch (IOException e) {
				// Keepalive.
				if (isAlive) {
					sendMessage("ISALIVE");
					isAlive = false;
				}
				else {
					QFLogger.INSTANCE.Log("No response from " + socket.getInetAddress());
					break;
				}
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
		
		server.removeClient(id);
 		QFLogger.INSTANCE.Log("Disconnect " + socket.getInetAddress());
	}

}