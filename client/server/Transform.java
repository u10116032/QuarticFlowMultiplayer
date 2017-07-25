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
}