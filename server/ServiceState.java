import java.util.*;
import java.util.concurrent.*;

public abstract class ServiceState {

	protected Map<String, RequestHandler> requestHandlerMap;

	public ServiceState(ConnectionService service)
	{
		requestHandlerMap = new ConcurrentHashMap<String, RequestHandler>();
		requestHandlerMap.put("\0", new HeartBeatHandler(service));
		requestHandlerMap.put("CLOSE", new CloseHandler(service));	
		requestHandlerMap.put("$", new StreamDataHandler(service));	
	}

	public void requestExecute(String requestType, byte[] data)
	{
		if(requestHandlerMap.containsKey(requestType))
			requestHandlerMap.get(requestType).execute(data);
	}

}