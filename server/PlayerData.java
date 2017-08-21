import java.io.*;

public class PlayerData{

	private Transform head;
	private Transform leftHand;
	private Transform rightHand;

	public PlayerData(Transform head, Transform leftHand, Transform rightHand)
	{
		this.head = head;
		this.leftHand = leftHand;
		this.rightHand = rightHand;
	}

	public static PlayerData parse(byte[] rawData) throws IOException {
		ByteArrayInputStream rawDataStream = new ByteArrayInputStream(rawData);
		DataInputStream dataStream = new DataInputStream(rawDataStream);

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

		return new PlayerData(head, leftHand, rightHand);
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

	public byte[] toByteArray()
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.write(head.toByteArray(), 0, head.toByteArray().length);
			dataWriter.write(leftHand.toByteArray(), 0, leftHand.toByteArray().length);
			dataWriter.write(rightHand.toByteArray(), 0, rightHand.toByteArray().length);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dataStream.toByteArray();
	}

	@Override
	public String toString()
	{
		String thisString = "head: " + head.toString() + 
		",leftHand: " + leftHand.toString() + 
		",rightHand: " + rightHand.toString();

		return thisString;
	}

}