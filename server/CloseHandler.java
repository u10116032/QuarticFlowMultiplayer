public class CloseHandler extends RequestHandler{
	
	public CloseHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(String token)
	{
		ClientData clientData = GameDatabase.INSTANCE.getClientData(service.getId());

		if (clientData == null){
			service.sendMessage("$ILLEGAL");
			service.closeSocket();
		}

		if(!clientData.getStatus()){
			service.sendMessage("$ILLEGAL");
			service.closeSocket();
		}

		service.closeSocket();
	}
}