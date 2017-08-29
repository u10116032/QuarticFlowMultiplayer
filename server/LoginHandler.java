import java.io.*;
import java.nio.*;

public class LoginHandler extends RequestHandler{

	private Thread sendThread;

	// TODO: execute()
	public LoginHandler(ConnectionService service)
	{
		super(service);
		sendThread = new Thread(new Runnable() {
            public void run() { 
                sendTask();
            } 
        });
	}

	public void execute(byte[] tokenByte)
	{
		int id = -1;
		try {
			String idString = new String(tokenByte, "UTF-8");
			id = Integer.parseInt(idString);
		}
		catch(NumberFormatException e) {
			QFLogger.INSTANCE.Log("Illegal Account");
			service.closeService();
			return;
		}
		catch(UnsupportedEncodingException e ){
			QFLogger.INSTANCE.Log("Illegal Account");
			service.closeService();
			return;
		}

		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);

		if (clientData == null){
			// Do not fetch the clientData of this id, close the current service
			service.closeService();
			return;
		}

		service.setId(id);

		if (clientData.isOnline()){
			// Check whether same service send twice LOGIN
			if (clientData.getService() == service)
				return;
			
			// Fetch the clientData of this id, close its' old service
			clientData.setOnline(false);
			clientData.getService().closeService();
			clientData.getService().stop();

			clientData.setService(service);
		}

		clientData.setOnline(true);
		clientData.setService(service);
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		String responseType = "SUCCESS";
		service.sendMessage(responseType.getBytes());

        sendThread.start();

        QFLogger.INSTANCE.Log("id: " + id + " log on");
	}

	private void sendTask()
	{
		int id = service.getId();

		long lastTime = System.currentTimeMillis( );
		while(GameDatabase.INSTANCE.getClientData(id).isOnline()){
			
			long currentTime = System.currentTimeMillis( );
			
			if (currentTime - lastTime < 33.0)
				continue;

			String requestType = "$ ";
			byte[] clientDataByte = GameDatabase.INSTANCE.toByteArray(id);

			byte[] packet = new byte[requestType.getBytes().length + clientDataByte.length];
			System.arraycopy(requestType.getBytes(), 0, packet, 0, requestType.getBytes().length);
			System.arraycopy(clientDataByte, 0, packet, requestType.getBytes().length, clientDataByte.length);

			service.sendMessage(packet);

			lastTime = currentTime;		
		}
		QFLogger.INSTANCE.Log("sendTask closed");
	}
}