import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Server{
	private final int SERVER_PORT = 40000;
	private ServerSocket serverSocket;

	private final int DATABASE_PORT = 41000;
	private DatagramSocket gameDataSocket;
	private Map<Integer, SocketAddress> clientMap;

	private GameDatabase gameDatabase;

	private List<Integer> idList;

 	private Server() throws Exception
 	{
		serverSocket = new ServerSocket(SERVER_PORT);
		gameDataSocket = new DatagramSocket(DATABASE_PORT);

		clientMap = new Hashtable<Integer, SocketAddress>();

		gameDatabase = new GameDatabase();
		idList = new ArrayList<Integer>();
		idList.add(0);
		idList.add(1);

		Thread receiveThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				receive();
			}
		});
		receiveThread.start();

		Thread sendThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				send();
			}
		});
		sendThread.start();
 	}

 	private void startServer ()
 	{
 		while (true) {
 			Socket socket = null;
			try {
 				socket = serverSocket.accept();
 				System.out.println("Establish connection with " + socket.getInetAddress());

				int newId = -1;
 				synchronized(idList) {
 					newId = idList.remove(0);
 				}

 				// Start service for new client. 				
 				ConnectionService connection = new ConnectionService(socket, this, newId);
 				connection.start();
 			}
 			catch (IOException e) {
 				System.out.println("IOException : " + e.toString());
 			}
 		}
 	}

 	public void removeClient(int id)
 	{
 		synchronized(idList) {
 			idList.add(id);
 		}

 		clientMap.remove(id);
 		gameDatabase.remove(id);
 	}

	private void receive()
	{
		byte[] buffer = new byte[256];

		try {
			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				gameDataSocket.receive(packet);

				ClientData clientData = ClientData.parse(buffer);

				clientMap.put(clientData.getId(), packet.getSocketAddress());
				gameDatabase.put(clientData.getId(), clientData);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send()
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		DataOutputStream dataWriter = new DataOutputStream(dataStream);
		long lastTime = System.currentTimeMillis();
		try {
			while (true) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastTime < 20)
					continue;

				lastTime = currentTime;

				byte[] buffer = null;

				try {	
					buffer = gameDatabase.toByteArray();
				}
				catch (IOException e) {
			 		e.printStackTrace();
			 		continue;
			 	}

				for (SocketAddress address : clientMap.values()) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
					gameDataSocket.send(packet);
				}
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Server server;
      try {
      	server = new Server();
      	server.startServer();
 		}
 		catch (Exception e) {
 			System.out.println("Error opening server");
 			System.out.println("Exception :" + e.toString());
 		}
   }
}
