import java.util.*;
import java.util.concurrent.*;

public class LoginState extends ServiceState{

	public LoginState(ConnectionService service)
	{
		super(service);

		int maxPlayerNumberPerRoom = WaitLineQueue.INSTANCE.getMaxPlayerNumberPerCount();

		boolean pairedSuccess = true;
		Iterator<ConnectionService> waitQueueIterator = WaitLineQueue.INSTANCE.getIterator();
		for (int i = 0; i < maxPlayerNumberPerRoom - 1; ++i){
			
			if(! waitQueueIterator.hasNext()){
				pairedSuccess = false;
				break;
			}
			
			ConnectionService otherService = waitQueueIterator.next();

			if (otherService == null || !GameDatabase.INSTANCE.getClientData(otherService.getId()).isOnline()){
				pairedSuccess = false;
				break;
			}
		}

		if (!pairedSuccess)
			WaitLineQueue.INSTANCE.enqueue(service);
		else {
			int roomNumber = WaitLineQueue.INSTANCE.getAvailableRoomNumber();

			Queue<ConnectionService> queueBuffer = new ConcurrentLinkedQueue<ConnectionService>();

			// Set pairId and roomNumber first
			for (int i = 0; i < maxPlayerNumberPerRoom - 1; ++i){
				ConnectionService otherService;
				do{
					otherService = WaitLineQueue.INSTANCE.dequeue();
				}while(otherService == null || !GameDatabase.INSTANCE.getClientData(otherService.getId()).isOnline());

				int otherServiceId = otherService.getId();
				ClientData otherClientData = GameDatabase.INSTANCE.getClientData(otherServiceId);

				otherClientData.setRoomNumber(roomNumber);
				otherClientData.setPairId(i);
				GameDatabase.INSTANCE.updateClientData(otherServiceId, otherClientData);

				queueBuffer.add(otherService);
			}

			int serviceId = service.getId();

			ClientData clientData = GameDatabase.INSTANCE.getClientData(serviceId);
			clientData.setRoomNumber(roomNumber);
			clientData.setPairId(maxPlayerNumberPerRoom - 1);
			GameDatabase.INSTANCE.updateClientData(serviceId, clientData);


			// Change all service's state into pairState
			for (int i = 0; i < queueBuffer.size(); ++i){
				ConnectionService otherService = queueBuffer.poll();
				otherService.setState(new PairState(roomNumber, otherService));
			}
			service.setState(new PairState(roomNumber, service));
		}
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}

}