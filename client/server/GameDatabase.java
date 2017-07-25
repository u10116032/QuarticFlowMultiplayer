import java.util.*;
import java.io.*;

public class GameDatabase {
	private Map<Integer, ClientData> clientDataMap;

	public GameDatabase()
	{
		clientDataMap = new Hashtable<Integer, ClientData>();
		Transform head = new Transform(0, 1, 0, 1, 0, 1, 0);
		Transform leftHand = new Transform(0, 1, 0, 1, 0, 1, 0);
		Transform rightHand = new Transform(0, 1, 0, 1, 0, 1, 0);
		put(0, new ClientData(0, head, leftHand, rightHand));
	}

	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		DataOutputStream dataWriter = new DataOutputStream(dataStream);
		
		for (ClientData clientData : clientDataMap.values()) {
			dataWriter.writeByte(clientData.getId());	

			Transform head = clientData.getHead();
			dataWriter.writeFloat(head.getPositionX());
			dataWriter.writeFloat(head.getPositionY());
			dataWriter.writeFloat(head.getPositionZ());
			dataWriter.writeFloat(head.getQuaternionX());
			dataWriter.writeFloat(head.getQuaternionY());
			dataWriter.writeFloat(head.getQuaternionZ());
			dataWriter.writeFloat(head.getQuaternionW());

			Transform leftHand = clientData.getLeftHand();
			dataWriter.writeFloat(leftHand.getPositionX());
			dataWriter.writeFloat(leftHand.getPositionY());
			dataWriter.writeFloat(leftHand.getPositionZ());
			dataWriter.writeFloat(leftHand.getQuaternionX());
			dataWriter.writeFloat(leftHand.getQuaternionY());
			dataWriter.writeFloat(leftHand.getQuaternionZ());
			dataWriter.writeFloat(leftHand.getQuaternionW());

			Transform rightHand = clientData.getLeftHand();
			dataWriter.writeFloat(rightHand.getPositionX());
			dataWriter.writeFloat(rightHand.getPositionY());
			dataWriter.writeFloat(rightHand.getPositionZ());
			dataWriter.writeFloat(rightHand.getQuaternionX());
			dataWriter.writeFloat(rightHand.getQuaternionY());
			dataWriter.writeFloat(rightHand.getQuaternionZ());
			dataWriter.writeFloat(rightHand.getQuaternionW());
		}
		
		return dataStream.toByteArray();
	}

	public void put(int id, ClientData clientData)
	{
		clientDataMap.put(id, clientData);
	}

	public void remove(int id)
	{
		clientDataMap.remove(id);
	}
}