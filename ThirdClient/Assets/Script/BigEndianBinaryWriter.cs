using System;
using System.IO;

public class BigEndianBinaryWriter : BinaryWriter {
	public BigEndianBinaryWriter(Stream stream) : base(stream) {}

	public override void Write(float value)
	{
		float[] floats = new[] { value };
		byte[] bytes = new byte[4];
		Buffer.BlockCopy(floats, 0, bytes, 0, 4);
		Array.Reverse (bytes);
		BaseStream.Write(bytes, 0, 4);
	}
}
