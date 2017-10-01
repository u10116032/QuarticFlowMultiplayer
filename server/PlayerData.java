import java.io.*;
import java.util.Optional;

public class PlayerData{
	private int score;

	private float breathDegree;
	private float breathHeight;

	private Transform head;
	private Transform leftHand;
	private Transform rightHand;

	public PlayerData(int score, float breathDegree, float breathHeight, Transform head, Transform leftHand, Transform rightHand)
	{
		this.score = score;

		this.breathDegree = breathDegree;
		this.breathHeight = breathHeight;

		this.head = head;
		this.leftHand = leftHand;
		this.rightHand = rightHand;
	}

	public static PlayerData parse(byte[] rawData){
		PlayerData playerData;
		try{
			ByteArrayInputStream rawDataStream = new ByteArrayInputStream(rawData);
			DataInputStream dataStream = new DataInputStream(rawDataStream);

			int score = Optional.ofNullable(dataStream.readInt()).orElse(0);

			float breathDegree = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);
			float breathHeight = Optional.ofNullable(dataStream.readFloat()).orElse(0.0f);

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

			playerData =  new PlayerData(score, breathDegree, breathHeight, head, leftHand, rightHand);
		}
		catch(Exception e){
			// e.printStackTrace();
			QFLogger.INSTANCE.Log("PlayerData parse error");
			playerData = null;
		}

		return playerData;
	}

	public int getScore()
	{
		return this.score;
	}

	public float getBreathDegree()
	{
		return this.breathDegree;
	}

	public float getBreathHeight()
	{
		return this.breathHeight;
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
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream(96);
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.writeInt(score);

			dataWriter.writeFloat(breathDegree);
			dataWriter.writeFloat(breathHeight);

			byte[] headByte = head.toByteArray();
			dataWriter.write(headByte, 0, headByte.length);

			byte[] leftHandByte = leftHand.toByteArray();
			dataWriter.write(leftHandByte, 0, leftHandByte.length);

			byte[] righthandByte = rightHand.toByteArray();
			dataWriter.write(righthandByte, 0, righthandByte.length);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dataStream.toByteArray();
	}

	@Override
	public String toString()
	{
		String thisString = "score: " + score + 
		", breathDegree: " + breathDegree +
		", breathHeight: " + breathHeight + 
		", head: " + head.toString() + 
		",leftHand: " + leftHand.toString() + 
		",rightHand: " + rightHand.toString();

		return thisString;
	}

}