import java.io.*;
import java.nio.charset.Charset;

public class StreamHandler extends RequestHandler{
	public StreamHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(String token)
	{
		// TODO: check status
		ClientData clientData = GameDatabase.INSTANCE.getClientData(service.getId());

		if (clientData == null){
			service.sendMessage("$ILLEGAL");
			service.closeSocket();
		}

		if (!clientData.getStatus()){
			service.sendMessage("$ILLEGAL");
			service.closeSocket();
		}

		try{
			clientData.setPlayerData(PlayerData.parse(token.getBytes(Charset.forName("UTF-8"))));
		}
		catch(IOException e){
			e.printStackTrace();
		}

		GameDatabase.INSTANCE.updateClientData(service.getId(), clientData);
	}
}