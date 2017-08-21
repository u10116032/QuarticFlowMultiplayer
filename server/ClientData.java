import java.io.*;

public class ClientData {

	private int id;
	private boolean status;

	PlayerData playerData;

	public ClientData(int id)
	{
		this.id = id;
		this.status = false;
	}

	public int getId()
	{
		return id;
	}

	public void setStatus(boolean status)
	{
		this.status = status;
	}

	public boolean getStatus()
	{
		return status;
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
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.writeInt(id);
			dataWriter.write(playerData.toByteArray(), 0, playerData.toByteArray().length);
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
		",status: " + status + 
		",playerData: " + playerData.toString();

		return thisString;
	}

}