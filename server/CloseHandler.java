public class CloseHandler extends RequestHandler{
	
	public CloseHandler(ConnectionService service)
	{
		super(service);
	}

	public void execute(byte[] tokenByte)
	{
		service.closeService();
	}
}