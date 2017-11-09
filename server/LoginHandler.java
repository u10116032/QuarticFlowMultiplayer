import java.io.*;
import java.nio.*;

public class LoginHandler extends RequestHandler{

	private ConnectionService service;

	public LoginHandler(ConnectionService service)
	{
		super(service);
		this.service = service;
	}

	public void execute(byte[] tokenByte)
	{
		int id = -1;
		try {
			String idString = new String(tokenByte, "UTF-8");
			id = Integer.parseInt(idString);
		}
		catch(NumberFormatException e) {
			QFLogger.INSTANCE.Log("Illegal account format error /" + service.getInetAddress());
			service.closeService();
			return;
		}
		catch(UnsupportedEncodingException e ){
			QFLogger.INSTANCE.Log("Illegal account format error /" + service.getInetAddress());
			service.closeService();
			return;
		}

		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);

		if (clientData == null){
			// Do not fetch the clientData of this id, close the current service
			QFLogger.INSTANCE.Log("The account " + id + " doesn't exist /" + service.getInetAddress());
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

		QFLogger.INSTANCE.Log("Login id: " + id + service.getInetAddress());
		service.setState(new LoginState(this.service));

		/*
		if (clientData.getRoomNumber() != -1 && clientData.getPairId() != -1){
			QFLogger.INSTANCE.Log("Relogin id: " + id + service.getInetAddress());
			service.setState(new PairState(clientData.getRoomNumber(), this.service));
		}
		else{
			QFLogger.INSTANCE.Log("Login id: " + id + service.getInetAddress());
			service.setState(new LoginState(this.service));
		}
		*/   
	}

	
}