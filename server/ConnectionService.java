import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// TODO: streaming

public class ConnectionService {
	private int id;

	private Socket socket;

	private DataInputStream reader;
	private DataOutputStream writer;

	// TODO: check if thread safe
	private Object streamLock = new Object();

	private Map<String, RequestHandler> requestHandlerMap;

	private Thread receiveThread;

	public ConnectionService (Socket socket) throws IOException
	{
		this.socket = socket;
		socket.setSoTimeout(1000);

		reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
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
	/**
	* [Packet Description]
	* Split with One Space
	* @param CommandType [ "", CLOSE, LOGIN, $ ]
	* @param Data        [ PlayerData in byte[] ]
	*/	
	private void receiveTask()
	{
		while (true) {
			try{
				byte[] requestBytes = readRequestLine();
				List<byte[]> tokenList = splitRequestLine(requestBytes);

				String requestType = new String(tokenList.get(0), "UTF-8");
				if (!requestHandlerMap.containsKey(requestType))
					closeService();
				else
					requestHandlerMap.get(requestType).execute(tokenList.get(1 % tokenList.size()));
			}
			catch(IOException e) {
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

	private byte[] readRequestLine() throws IOException
	{
		byte[] buffer = new byte[128];
		int bufferCount = 0;
		byte receivedByte;
		while (true) {
			receivedByte = reader.readByte();
			if (receivedByte == '\r') {
				receivedByte = reader.readByte();
				if(receivedByte == '\n')
					return Arrays.copyOf(buffer, bufferCount);
			}
			buffer[bufferCount++] = receivedByte;
		}
	}

	private List<byte[]> splitRequestLine(byte[] requestBytes)
	{
		List<byte[]> tokenList = new ArrayList<byte[]>();

		int delimIndex = -1;
		for (int i = 0; i < requestBytes.length; ++i) {
			if (requestBytes[i] == ' ') {
				delimIndex = i;
				break;
			}
		}

		tokenList.add(Arrays.copyOf(requestBytes, delimIndex - 1));
		tokenList.add(Arrays.copyOfRange(requestBytes, delimIndex + 1, requestBytes.length));

		return tokenList;
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
			
			try{
				writer.write(message.getBytes());
			}
			catch(IOException e){
			
			}

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