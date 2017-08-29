import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public enum GameDatabase {
	INSTANCE;

	private byte gameState;
	private Map<Integer, ClientData> clientDataMap;

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
			// label in first row
		    String line = fileReader.readLine();

		    while (true) {
		        line = fileReader.readLine();

		        if (line == null)
		        	break;
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
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream(256);
		DataOutputStream dataWriter = new DataOutputStream(dataStream);
		
		try{
			for (ClientData clientData : clientDataMap.values()) {
				if (clientData.getId() != selfId && clientData.isOnline())
					dataWriter.write(clientData.toByteArray());
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dataStream.toByteArray();
	}

	public void updateClientData(int id, ClientData clientData)
	{
		if(!clientDataMap.containsKey(id))
			return;

		clientDataMap.put(id, clientData);	
	}

	public ClientData getClientData(int id)
	{
		
		if(!clientDataMap.containsKey(id))
			return null;

		return clientDataMap.get(id);
		
	}
}