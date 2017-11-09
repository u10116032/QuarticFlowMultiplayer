public class CloseHandler extends RequestHandler{
	
	public CloseHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(byte[] tokenByte)
	{
		int id = service.getId();

		ClientData clientData = GameDatabase.INSTANCE.getClientData(id);
		
		if(clientData == null)
			return;

		if (clientData.getRoomNumber() != -1 && clientData.getPairId() != -1){
			clientData.setPairId(-1);

			int roomNumber = clientData.getRoomNumber();
			WaitLineQueue.INSTANCE.releaseRoomNumber(roomNumber);
			clientData.setRoomNumber(-1); 	
		}

		clientData.setStatus(0);
		GameDatabase.INSTANCE.updateClientData(id, clientData);

		service.closeService();
		QFLogger.INSTANCE.Log("Service Logout /id: " + service.getId());

	}
}