import java.io.*;
import java.util.Optional;

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

		float positionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		float positionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		float positionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

		float quaternionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		float quaternionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		float quaternionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		float quaternionW = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

		Transform head = new Transform(positionX, positionY, positionZ, quaternionX, quaternionY, quaternionZ, quaternionW);
		positionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		positionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		positionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

		quaternionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionW = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

		Transform leftHand = new Transform(positionX, positionY, positionZ, quaternionX, quaternionY, quaternionZ, quaternionW);

		positionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		positionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		positionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

		quaternionX = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionY = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionZ = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
		quaternionW = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

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