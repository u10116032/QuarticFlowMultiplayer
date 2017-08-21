import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// TODO: streaming

public class ConnectionService {
	private final int MAX_REQUEST_LENGTH = 16;

	private int id;

	private Socket socket;

	private BufferedReader reader;
	private PrintWriter writer;

	private Object writerLock = new Object();

	private Map<String, RequestHandler> requestMap;

	public ConnectionService (Socket socket) throws IOException
	{
		this.socket = socket;
		socket.setSoTimeout(1000);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
		
		requestMap = new ConcurrentHashMap<String, RequestHandler>();
		requestMap.put("", new HeartBeatHandler(this));
		requestMap.put("CLOSE", new CloseHandler(this));
		requestMap.put("LOGIN", new LoginHandler(this));
		requestMap.put("$", new StreamHandler(this));

		this.id = -1;

		Thread receiveThread = new Thread(new Runnable() {
            public void run() { 
                receiveTask();
            } 
        });
        receiveThread.start();

        QFLogger.INSTANCE.Log("Establish connection with " + socket.getInetAddress());
	}

	private void receiveTask()
	{
		while (true) {

			/**
			 * [Packet Description]
			 * Split with One Space
			 * @param CommandType [ "", CLOSE, LOGIN, $ ]
			 * @param Data        [ PlayerData in byte[] ]
			 */
			String receivePacket = null;

			try {
				receivePacket = reader.readLine();
				
				if (receivePacket == null)
					break;

				String[] tokens = receivePacket.split(" ");

				if (!requestMap.containsKey(tokens[0]) || tokens.length !=2){
					sendMessage("$ILLEGAL");	
					closeSocket();
				}
				else
					requestMap.get(tokens[0]).execute(tokens[1]);

				QFLogger.INSTANCE.Log("Received: " + receivePacket);
			}
			catch (IOException e) {
				QFLogger.INSTANCE.Log("No response from " + socket.getInetAddress());
				break;
	 		}
		}

		try {

			if (id != -1){
				ClientData clientData = GameDatabase.INSTANCE.getClientData(id);
				clientData.setStatus(false);
				GameDatabase.INSTANCE.updateClientData(id, clientData);
			}

			reader.close();
		 	reader = null;

		 	writer.close();
			writer = null;

			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
 		QFLogger.INSTANCE.Log("Disconnect " + socket.getInetAddress());
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public void sendMessage(String message)
	{
		synchronized(writerLock)
		{
			if (writer == null)
				return;
			
			writer.println(message);

			QFLogger.INSTANCE.Log("Send: " + message);
		}
	}

	public void closeSocket()
	{
		if (socket == null)
			return;

		try{
			socket.shutdownInput();
			socket.shutdownOutput();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}