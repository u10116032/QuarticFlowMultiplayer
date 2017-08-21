import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public enum GameDatabase {
	INSTANCE;

	private byte gameState;
	private Map<Integer, ClientData> clientDataMap;

	private Object databaseLock = new Object();

	private static final String DATABASE_PATH = "./database.txt";

	GameDatabase() 
	{
		gameState = (byte)0;
		clientDataMap = new ConcurrentHashMap<Integer, ClientData>();	

		try{
			readDatabase(DATABASE_PATH);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private void readDatabase(String databasePath) throws IOException
	{
		BufferedReader fileReader = new BufferedReader(new FileReader(databasePath));
		
		try {
		    String line = fileReader.readLine();

		    while (line != null) {
		        line = fileReader.readLine();

		        ClientData clientData = new ClientData(Integer.parseInt(line));
		        clientDataMap.put(clientData.getId(), clientData);
		    }
		} 
		finally {
		    fileReader.close();
		}
	}

	public byte[] toByteArray(int selfId)
	{
		synchronized(databaseLock)
		{
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			DataOutputStream dataWriter = new DataOutputStream(dataStream);
			
			try{
				for (ClientData clientData : clientDataMap.values()) {
					if (clientData.getId() != selfId)
						dataWriter.write(clientData.toByteArray());
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			return dataStream.toByteArray();
		}
	}

	public void updateClientData(int id, ClientData clientData)
	{
		synchronized(databaseLock)
		{
			if(!clientDataMap.containsKey(id))
				return;

			clientDataMap.put(id, clientData);
		}
	}

	public ClientData getClientData(int id)
	{
		synchronized(databaseLock)
		{
			if(!clientDataMap.containsKey(id))
				return null;

			return clientDataMap.get(id);
		}
	}
}