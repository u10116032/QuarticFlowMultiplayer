import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ConnectionService {
	private int id;

	private Socket socket;

	private DataInputStream reader;
	private DataOutputStream writer;

	private Map<String, RequestHandler> requestHandlerMap;

	private Thread receiveThread;

	private ServiceState state;

	public ConnectionService (Socket socket) throws IOException
	{
		state = new InitialState(this);

		this.socket = socket;
		socket.setSoTimeout(10000);

		reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		writer = new DataOutputStream(socket.getOutputStream());

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
	* Split with One Space.
	* In the end of the packet, add '\r' and '\n' in the end for tokenizer.
	* @param CommandType [ "", CLOSE, LOGIN, $ ]
	* @param Data        [ PlayerData in byte[] ]
	*/	
	private void receiveTask()
	{
		while (true) {
			try{
				byte[] requestBytes = readRequestLine();
				if (requestBytes == null)
					continue;
				
				List<byte[]> tokenList = splitRequestLine(requestBytes);
				if (tokenList.size() == 0)
					continue;

				String requestType = new String(tokenList.get(0), "UTF-8");
				state.requestExecute(requestType, tokenList.get(1 % tokenList.size()));

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

			if (WaitLineQueue.INSTANCE.containKey(this))
				WaitLineQueue.INSTANCE.remove(this);

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

	private byte[] readRequestLine() throws IOException
	{
		byte[] buffer = new byte[128];
		int bufferCount = 0;
		byte receivedByte;
		for (int i = 0; i < buffer.length; ++i) {
			receivedByte = reader.readByte();
			if (receivedByte == '\r') {
				receivedByte = reader.readByte();
				if(receivedByte == '\n') {
					return Arrays.copyOf(buffer, bufferCount);
				}
			}
			buffer[bufferCount++] = receivedByte;
		}

		return null;
	}

	private static List<byte[]> splitRequestLine(byte[] requestBytes) throws NullPointerException
	{
		List<byte[]> tokenList = new ArrayList<byte[]>();

		int delimIndex = 0;
		for (int i = 0; i < requestBytes.length; ++i) {
			if (requestBytes[i] == ' ') {
				tokenList.add(Arrays.copyOfRange(requestBytes, delimIndex, i));
				delimIndex = i+1;
				break;
			}
		}

		if (delimIndex != requestBytes.length)
			tokenList.add(Arrays.copyOfRange(requestBytes, delimIndex, requestBytes.length));

		return tokenList;
	}

	public void setState(ServiceState state)
	{
		this.state = state;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public void sendMessage(byte[] packet)
	{
		if (writer == null)
			return;
		
		try{
			byte[] clrf = new byte[2];
			clrf[0] = '\r';
			clrf[1] = '\n';
			writer.write(packet, 0, packet.length);
			writer.write(clrf, 0, clrf.length);
			writer.flush();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		// try{
		// 	QFLogger.INSTANCE.Log("Send: " + new String(packet, "UTF-8"));
		// }
		// catch(UnsupportedEncodingException  e){
		// 	e.printStackTrace();
		// }
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

	public InetAddress getInetAddress()
	{
		return socket.getInetAddress();
	}

	@Override
	public boolean equals(Object object) {
	    if (object instanceof ConnectionService) {
	        ConnectionService otherService = (ConnectionService) object;
	        return this.id == otherService.getId();
	    }

	    return false;
	}

	@Override
	public int hashCode() {
	    return Integer.hashCode(this.id);
	}

	@Override
	public String toString() {
		String thisString = "id" + id + 
			"state: " + state.toString();
	    return thisString;
	}
}