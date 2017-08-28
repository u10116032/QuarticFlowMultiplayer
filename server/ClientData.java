import java.io.*;

public class ClientData {

	private int id;
	private boolean online;

	private PlayerData playerData;

	private ConnectionService service;

	public ClientData(int id)
	{
		this.id = id;
		this.online = false;

		this.service = null;
	}

	public void setService(ConnectionService service)
	{
		this.service = service;
	}

	public ConnectionService getService()
	{
		return this.service;
	}

	public int getId()
	{
		return id;
	}

	public void setOnline(boolean online)
	{
		this.online = online;
	}

	public boolean isOnline()
	{
		return online;
	}

	public void setPlayerData(PlayerData playerData)
	{
		this.playerData = playerData;
	}
	
	public PlayerData getPlayerData()
	{
		return playerData;
	}

	public byte[] toByteArray()
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream(85);
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.writeByte(id);
			byte[] playerDataBytes = playerData.toByteArray();
			dataWriter.write(playerDataBytes, 0, playerDataBytes.length);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dataStream.toByteArray();
	}

	@Override
	public String toString()
	{
		String thisString = "id: " + id + 
		",online: " + online + 
		",playerData: " + playerData.toString();

		return thisString;
	}

}