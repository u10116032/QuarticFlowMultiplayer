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
		clientData.setStatus(true);
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		service.sendMessage("$SUCCESS");

		Thread sendThread = new Thread(new Runnable() {
            public void run() { 
                while(GameDatabase.INSTANCE.getClientData(id).getStatus()){
					try{
						service.sendMessage(new String(GameDatabase.INSTANCE.toByteArray(id), "UTF-8"));
					}
					catch(IOException e){
						e.printStackTrace();
					}
				}
            } 
        });
        sendThread.start();

        QFLogger.INSTANCE.Log("id: " + id + "Login");
	}
}