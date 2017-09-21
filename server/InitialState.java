public class InitialState extends ServiceState {
	
	public InitialState(ConnectionService service)
	{
		super(service);
		requestHandlerMap.put("LOGIN", new LoginHandler(service));
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}