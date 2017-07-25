import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Server{
	private final int SERVER_PORT = 40000;
	private ServerSocket serverSocket;

	private final int DATABASE_PORT = 41000;
	private DatagramSocket gameDataSocket;
	private List<InetAddress> clientAddressList;

	private GameDatabase gameDatabase;

	private List<Integer> idList;

 	private Server() throws Exception
 	{
		serverSocket = new ServerSocket(SERVER_PORT);
		gameDataSocket = new DatagramSocket(DATABASE_PORT);

		clientAddressList = new ArrayList<InetAddress>();

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

 				InetAddress clientAddress = socket.getInetAddress();
 				
 				synchronized(clientAddressList) {
 					clientAddressList.add(clientAddress);
 				}


 				// Start service for new client. 				
 				ConnectionService connection = new ConnectionService(socket, clientAddress, this, idList.get(0));
 				idList.remove(0);
 				connection.start();
 			}
 			catch (IOException e) {
 				System.out.println("IOException : " + e.toString());
 			}
 		}
 	}

 	public void removeClient(InetAddress clientAddress, int id)
 	{
 		synchronized(clientAddressList) {
 			clientAddressList.remove(clientAddress);
 			gameDatabase.remove(id);
 			idList.add(id);
 			System.out.println(id);
 		}
 	}

	private void receive()
	{
		byte[] buffer = new byte[256];

		try {
			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				gameDataSocket.receive(packet);
				ClientData clientData = ClientData.parse(buffer);

				if (clientData != null) {
					//System.out.println(clientData.toString());
					gameDatabase.put(clientData.getId(), clientData);
				}
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
				synchronized(clientAddressList) {
					for (InetAddress address : clientAddressList) {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, DATABASE_PORT);
						gameDataSocket.send(packet);
						//System.out.println("send");
					}
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
