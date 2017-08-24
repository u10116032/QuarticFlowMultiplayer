import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// TODO: streaming

public class ConnectionService {
	private int id;

	private Socket socket;

	private BufferedReader reader;
	private PrintWriter writer;

	// TODO: check if thread safe
	private Object streamLock = new Object();

	private Map<String, RequestHandler> requestHandlerMap;

	private Thread receiveThread;

	public ConnectionService (Socket socket) throws IOException
	{
		this.socket = socket;
		socket.setSoTimeout(1000);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
		
		requestHandlerMap = new ConcurrentHashMap<String, RequestHandler>();
		requestHandlerMap.put("", new HeartBeatHandler(this));
		requestHandlerMap.put("CLOSE", new CloseHandler(this));
		requestHandlerMap.put("LOGIN", new LoginHandler(this));
		requestHandlerMap.put("$", new StreamDataHandler(this));

		this.id = -1;

		receiveThread = new Thread(new Runnable() {
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
				QFLogger.INSTANCE.Log("Received: " + receivePacket);
				if (receivePacket == null)
					break;

				String[] tokens = receivePacket.split(" ");

				if (!requestHandlerMap.containsKey(tokens[0]))
					closeService();
				else
					requestHandlerMap.get(tokens[0]).execute(tokens[1 % tokens.length]);

				
			}
			catch (IOException e) {
				QFLogger.INSTANCE.Log("No response from " + socket.getInetAddress());
				break;
	 		}
		}

		try {
			ClientData clientData = GameDatabase.INSTANCE.getClientData(id);
			if (clientData != null){
				clientData.setOnline(false);
				clientData.setService(null);
				GameDatabase.INSTANCE.updateClientData(id, clientData);
			}

			synchronized(streamLock) {
				reader.close();
			 	reader = null;

			 	writer.close();
				writer = null;

				socket.close();
			}
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
		synchronized(streamLock) {
			if (writer == null)
				return;
			
			writer.println(message);

			QFLogger.INSTANCE.Log("Send: " + message);
		}
	}

	public void closeService()
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

	public void stop()
	{
		if(receiveThread == null)
			return;

		try{
			receiveThread.join();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}