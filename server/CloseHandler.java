public class CloseHandler extends RequestHandler{
	
	public CloseHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(byte[] tokenByte)
	{
		int id = service.getId();

		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);
		
		clientData.setPairId(-1);

		int roomNumber = clientData.getRoomNumber();
		WaitLineQueue.INSTANCE.releaseRoomNumber(roomNumber);
		clientData.setRoomNumber(-1); 

		GameDatabase.INSTANCE.updateClientData(id, clientData);

		service.closeService();
		QFLogger.INSTANCE.Log("Service Logout /id: " + service.getId());

	}
}