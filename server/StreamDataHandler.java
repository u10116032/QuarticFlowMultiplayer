import java.io.*;
import java.nio.charset.Charset;

public class StreamDataHandler extends RequestHandler{
	public StreamDataHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(byte[] tokenByte)
	{
		ClientData clientData = GameDatabase.INSTANCE.getClientData(service.getId());
		
		if (clientData == null || !clientData.isOnline()){
			service.closeService();
			return;
		}
	
		PlayerData playerData = PlayerData.parse(tokenByte);
		if (playerData == null)
			playerData = GameDatabase.INSTANCE.getClientData(service.getId()).getPlayerData();
		clientData.setPlayerData(playerData);

		GameDatabase.INSTANCE.updateClientData(service.getId(), clientData);
	}
}