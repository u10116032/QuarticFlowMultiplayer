import java.util.concurrent.*;
import java.util.*;

public enum WaitLineQueue {

	INSTANCE;

	private Queue<ConnectionService> waitLine;
	private Map<Integer, Integer> availableRoomNumberMap;
	private int roomMaxNumber = 10;
	private int maxPlayerNumberPerRoom = 2;

	WaitLineQueue()
	{
		this.waitLine = new ConcurrentLinkedQueue<ConnectionService>();
		this.availableRoomNumberMap = new ConcurrentHashMap<Integer, Integer>();
		for (int i = 0; i < roomMaxNumber; ++i)
			availableRoomNumberMap.put(i, 0);
	}

	public void enqueue(ConnectionService service)
	{
		waitLine.add(service);
	}

	public ConnectionService dequeue()
	{
		return waitLine.poll();
	}

	public boolean containKey(ConnectionService service)
	{
		return waitLine.contains(service);
	}

	public boolean remove(ConnectionService service)
	{
		return waitLine.remove(service);
	}

	public Iterator<ConnectionService> getIterator()
	{
		return waitLine.iterator();
	}

	public int getMaxPlayerNumberPerCount()
	{
		return this.maxPlayerNumberPerRoom;
	}

	public int getAvailableRoomNumber()
	{
		int availableNumber = -1;

		for (int i = 0; i < roomMaxNumber; ++i){
			if (availableRoomNumberMap.get(i) == 0){
				availableNumber = i;
				break;
			}
		}

		availableRoomNumberMap.replace(availableNumber, 2); // The number of players for each room.

		return availableNumber;
	}

	public void releaseRoomNumber(int i)
	{
		availableRoomNumberMap.replace(i, availableRoomNumberMap.get(i) - 1);
	}

}