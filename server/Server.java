import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*;

public class Server{
	private final int SERVER_PORT = 40000;
	private ServerSocket serverSocket;

	private final int DATABASE_PORT = 41000;
	private DatagramSocket gameDataSocket;
	private Map<Integer, SocketAddress> clientMap;

	private GameDatabase gameDatabase;

	private Map<Integer, Boolean> idMap;
	private Object idMapLock;

 	private Server() throws Exception
 	{
		serverSocket = new ServerSocket(SERVER_PORT);
		gameDataSocket = new DatagramSocket(DATABASE_PORT);

		clientMap = new ConcurrentHashMap<Integer, SocketAddress>();

		gameDatabase = new GameDatabase();

		idMapLock = new Object();
		idMap = new ConcurrentHashMap<Integer, Boolean>();
		idMap.put(0, false);
		idMap.put(1, false);

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

 				// Start service for new client. 				
 				ConnectionService connection = new ConnectionService(socket, this);
 				connection.start();
 			}
 			catch (IOException e) {
 				System.out.println("IOException : " + e.toString());
 			}
 		}
 	}

 	public int getId()
 	{
 		synchronized(idMapLock) {
			for (ConcurrentHashMap.Entry<Integer, Boolean> entry : idMap.entrySet()) {
				if (entry.getValue().equals(false)) {
					int id = entry.getKey();
					idMap.replace(id, true);
					return id;
				}
	      }
	   }
 		
 		return -1;
 	}

 	public void removeClient(int id)
 	{
 		synchronized(idMapLock) {
 			idMap.replace(id, false);
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
