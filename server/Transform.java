import java.io.*;

public class Transform {
	private float positionX, positionY, positionZ;
	private float quaternionX, quaternionY, quaternionZ, quaternionW;

	public Transform(float positionX, float positionY, float positionZ,
		float quaternionX, float quaternionY, float quaternionZ,float quaternionW)
	{
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;

		this.quaternionX = quaternionX;
		this.quaternionY = quaternionY;
		this.quaternionZ = quaternionZ;
		this.quaternionW = quaternionW;
	}

	public float getPositionX()
	{
		return positionX;
	}

	public float getPositionY()
	{
		return positionY;
	}

	public float getPositionZ()
	{
		return positionZ;
	}

	public float getQuaternionX()
	{
		return quaternionX;
	}

	public float getQuaternionY()
	{
		return quaternionY;
	}

	public float getQuaternionZ()
	{
		return quaternionZ;
	}

	public float getQuaternionW()
	{
		return quaternionW;
	}

	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		DataOutputStream dataWriter = new DataOutputStream(dataStream);

		try{
			dataWriter.writeFloat(positionX);
			dataWriter.writeFloat(positionY);
			dataWriter.writeFloat(positionZ);

			dataWriter.writeFloat(quaternionX);
			dataWriter.writeFloat(quaternionY);
			dataWriter.writeFloat(quaternionZ);
			dataWriter.writeFloat(quaternionW);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dataStream.toByteArray();
	}

	@Override
	public String toString()
	{
		String thisString = "(" + positionX + ", " + positionY + ", "  + positionZ + ")" +
		"(" + quaternionX + ", " + quaternionY + ", " + quaternionZ + ", " + quaternionW + ")";

		return thisString;
	}
}