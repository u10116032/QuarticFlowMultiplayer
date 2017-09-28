import java.io.*;

public class ClientData {
	
	// Database Entry

	private int id;
	private int pairId;
	private int roomNumber;
	private boolean online;

	private PlayerData playerData;

	private ConnectionService service;

	public ClientData(int id)
	{
		this.id = id;
		this.pairId = -1; // -1 represent non paired.
		this.roomNumber = -1; // -1 represent non paired.
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

	public void setRoomNumber(int roomNumber)
	{
		this.roomNumber = roomNumber;
	}

	public int getRoomNumber()
	{
		return roomNumber;
	}

	public void setPairId(int pairId)
	{
		this.pairId = pairId;
	}

	public int getPairId()
	{
		return this.pairId;
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
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream(95);
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.writeByte(id);
			dataWriter.writeByte(pairId);
			
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
		"pairId: " + pairId +
		"roomNumber: " + roomNumber + 
		"online" + online +
		",playerData: " + playerData.toString();

		return thisString;
	}

}