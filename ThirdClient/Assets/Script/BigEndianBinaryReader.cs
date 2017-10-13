using System;
using System.IO;

public class BigEndianBinaryReader : BinaryReader {
	public BigEndianBinaryReader(Stream stream) : base(stream) {}

	public override float ReadSingle()
	{
		byte[] data = base.ReadBytes(4);
		return BitConverter.ToSingle(new byte[4]{data[3], data[2], data[1], data[0]}, 0);
	}
}