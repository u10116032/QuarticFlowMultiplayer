import java.io.*;

public class ClientData {
	private int id;

	private Transform head;
	private Transform leftHand;
	private Transform rightHand;

	public ClientData(int id, Transform head, Transform leftHand, Transform rightHand)
	{
		this.id = id;
		this.head = head;
		this.leftHand = leftHand;
		this.rightHand = rightHand;
	}

	public static ClientData parse(byte[] rawData) throws IOException {
		ByteArrayInputStream rawDataStream = new ByteArrayInputStream(rawData);
		DataInputStream dataStream = new DataInputStream(rawDataStream);

		int id = dataStream.readByte();

		float positionX = dataStream.readFloat();
		float positionY = dataStream.readFloat();
		float positionZ = dataStream.readFloat();

		float quaternionX = dataStream.readFloat();
		float quaternionY = dataStream.readFloat();
		float quaternionZ = dataStream.readFloat();
		float quaternionW = dataStream.readFloat();

		Transform head = new Transform(positionX, positionY, positionZ, quaternionX, quaternionY, quaternionZ, quaternionW);

		positionX = dataStream.readFloat();
		positionY = dataStream.readFloat();
		positionZ = dataStream.readFloat();

		quaternionX = dataStream.readFloat();
		quaternionY = dataStream.readFloat();
		quaternionZ = dataStream.readFloat();
		quaternionW = dataStream.readFloat();

		Transform leftHand = new Transform(positionX, positionY, positionZ, quaternionX, quaternionY, quaternionZ, quaternionW);

		positionX = dataStream.readFloat();
		positionY = dataStream.readFloat();
		positionZ = dataStream.readFloat();

		quaternionX = dataStream.readFloat();
		quaternionY = dataStream.readFloat();
		quaternionZ = dataStream.readFloat();
		quaternionW = dataStream.readFloat();

		Transform rightHand = new Transform(positionX, positionY, positionZ, quaternionX, quaternionY, quaternionZ, quaternionW);

		return new ClientData(id, head, leftHand, rightHand);
	}

	public int getId()
	{
		return id;
	}

	public Transform getHead()
	{
		return head;
	}

	public Transform getLeftHand()
	{
		return leftHand;
	}

	public Transform getRightHand()
	{
		return rightHand;
	}

	@Override
	public String toString()
	{
		String thisString = "id:" + id + 
		"head: " + head.toString() + 
		"leftHand: " + leftHand.toString() + 
		"rightHand: " + rightHand.toString();

		return thisString;
	}
}