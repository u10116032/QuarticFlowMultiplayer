public abstract class RequestHandler {

	protected ConnectionService service;

	public RequestHandler(ConnectionService service)
	{
		this.service = service;
	}

	public abstract void execute(byte[] tokenByte);
}