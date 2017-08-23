import java.io.*;

public class LoginHandler extends RequestHandler{

	// TODO: execute()
	public LoginHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(String token)
	{
		int id = Integer.parseInt(token);

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
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		service.sendMessage("SUCCESS");

		Thread sendThread = new Thread(new Runnable() {
            public void run() { 
                sendTask(id);
            } 
        });
        sendThread.start();

        QFLogger.INSTANCE.Log("id: " + id + "Login");
	}

	private void sendTask(int id)
	{
		while(GameDatabase.INSTANCE.getClientData(id).isOnline()){
			try{
				service.sendMessage(new String(GameDatabase.INSTANCE.toByteArray(id), "UTF-8"));
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}