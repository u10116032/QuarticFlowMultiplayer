import java.lang.*;

public class PairState extends ServiceState {

	private int roomNumber;
	private int pairId;
	private ConnectionService service;

	private Thread sendThread;

	public PairState(int roomNumber, ConnectionService service)
	{
		super(service);
		this.service = service;
		
		this.pairId = GameDatabase.INSTANCE.getClientData(service.getId()).getPairId();
		this.roomNumber = roomNumber;
		// requestHandlerMap.put("$", new StreamDataHandler(service));	

		sendThread = new Thread(new Runnable() {
            public void run() { 
                sendTask();
            } 
        });

        String requestType = "PAIRID ";
		byte[] packet = new byte[requestType.getBytes().length + 1];
		System.arraycopy(requestType.getBytes(), 0, packet, 0, requestType.getBytes().length);
		packet[packet.length - 1] = (byte)this.pairId;
		service.sendMessage(packet);

        sendThread.start();
	}

	private void sendTask()
	{	
		int id = service.getId();

		long lastTime = System.currentTimeMillis( );
		while(GameDatabase.INSTANCE.getClientData(id).isOnline()){
			
			long currentTime = System.currentTimeMillis( );
			
			if (currentTime - lastTime < 33.0f)
				continue;

			String requestType = "$ ";
			byte[] clientDataByte = GameDatabase.INSTANCE.toByteArray(id, roomNumber);
			
			byte[] packet = new byte[requestType.getBytes().length + clientDataByte.length];
			System.arraycopy(requestType.getBytes(), 0, packet, 0, requestType.getBytes().length);
			System.arraycopy(clientDataByte, 0, packet, requestType.getBytes().length, clientDataByte.length);

			service.sendMessage(packet);

			lastTime = currentTime;		
		}
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}