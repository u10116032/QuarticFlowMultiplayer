public class CloseHandler extends RequestHandler{
	
	public CloseHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(byte[] tokenByte)
	{
		service.closeService();
		int id = service.getId();
		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);
		clientData.setPairId(-1);
		clientData.setRoomNumber(-1); 
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		QFLogger.INSTANCE.Log("Service Logout /id: " + service.getId());

	}
}