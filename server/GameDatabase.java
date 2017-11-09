import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public enum GameDatabase {
	INSTANCE;

	private Map<Integer, ClientData> clientDataMap;

	private static final String DATABASE_PATH = "./database.txt";

	GameDatabase() 
	{
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

	public byte[] toByteArray(int selfId, int roomId)
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream(1024);
		DataOutputStream dataWriter = new DataOutputStream(dataStream);
		
		try{
			for (ClientData clientData : clientDataMap.values()) {
				if (clientData.getPlayerData() != null &&clientData != null && clientData.getId() != selfId && clientData.getRoomNumber() == roomId && clientData.isOnline())
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

	public List<ClientData> getClientDataByRoomNumber(int selfId, int roomId)
	{
		List<ClientData> clientDatas = new ArrayList<>();

		for (ClientData clientData : clientDataMap.values()){
			if (clientData.getRoomNumber() == roomId && clientData.getId() != selfId)
				clientDatas.add(clientData);
		}

		return clientDatas;
	}
}