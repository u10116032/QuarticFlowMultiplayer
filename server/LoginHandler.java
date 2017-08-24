import java.io.*;

public class LoginHandler extends RequestHandler{

	// TODO: execute()
	public LoginHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(String token)
	{
		int id = -1;
		try {
			id = Integer.parseInt(token);
		}
		catch(NumberFormatException e) {
			QFLogger.INSTANCE.Log("Ilegal account.");
			service.closeService();
			return;
		}

		service.setId(id);

		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);

		if (clientData == null){
			// Do not fetch the clientData of this id, close the current service
			service.closeService();
			return;
		}

		if (clientData.isOnline()){
			// Fetch the clientData of this id, close its' old service
			clientData.setOnline(false);
			clientData.getService().closeService();
			clientData.getService().stop();

			// Check if same service send twice LOGIN??
			if (clientData.getService() == service)
				return;

			clientData.setService(service);
		}

		clientData.setOnline(true);
		clientData.setService(service);
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		service.sendMessage("SUCCESS");

		Thread sendThread = new Thread(new Runnable() {
            public void run() { 
                sendTask();
            } 
        });
        sendThread.start();

        QFLogger.INSTANCE.Log("id: " + id + " Login");
	}

	private void sendTask()
	{
		int id = service.getId();

		while(GameDatabase.INSTANCE.getClientData(id).isOnline()){
			try{
				// May send after setOnline(false), but it doesn't matter
				service.sendMessage("$ "+ new String(GameDatabase.INSTANCE.toByteArray(id), "UTF-8"));
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}