using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System;

public class ClientData {
	public byte id;
	public Vector3 headPosition { get; set;}
	public Quaternion headPose { get; set;}

	public Vector3 leftHandPosition { get; set;}
	public Quaternion leftHandPose { get; set;}

	public Vector3 rightHandPosition { get; set;}
	public Quaternion rightHandPose { get; set;}

	public ClientData()
	{
		id = 0;
		headPosition = new Vector3 ();
		headPose = new Quaternion ();
		leftHandPosition = new Vector3 ();
		leftHandPose = new Quaternion ();
		rightHandPosition = new Vector3 ();
		rightHandPose = new Quaternion ();
	}

	public ClientData(byte id, Vector3 headPosition, Quaternion headPose, 
		Vector3 leftHandPosition, Quaternion leftHandPose, Vector3 rightHandPosition, Quaternion rightHandPose)
	{
		this.id = id;
		this.headPosition = headPosition;
		this.headPose = headPose;

		this.leftHandPosition = leftHandPosition;
		this.leftHandPose = leftHandPose;

		this.rightHandPosition = rightHandPosition;
		this.rightHandPose = rightHandPose;
	}

	public static List<ClientData> Parse(byte[] rawData)
	{
		List<ClientData> clientDataList = new List<ClientData>();

		MemoryStream stream = new MemoryStream(rawData);
		BigEndianBinaryReader reader = new BigEndianBinaryReader(stream);

		while (true) {
			try {
				byte clientId = reader.ReadByte();

				float pX = reader.ReadSingle();
				float pY = reader.ReadSingle();
				float pZ = reader.ReadSingle();
				Vector3 headPosition = new Vector3(pX, pY, pZ);

				float qX = reader.ReadSingle();
				float qY = reader.ReadSingle();
				float qZ = reader.ReadSingle();
				float qW = reader.ReadSingle();
				Quaternion headPose= new Quaternion(qX, qY, qZ, qW);

				pX = reader.ReadSingle();
				pY = reader.ReadSingle();
				pZ = reader.ReadSingle();
				Vector3 leftHandPosition = new Vector3(pX, pY, pZ);

				qX = reader.ReadSingle();
				qY = reader.ReadSingle();
				qZ = reader.ReadSingle();
				qW = reader.ReadSingle();
				Quaternion leftHandPose = new Quaternion(qX, qY, qZ, qW);

				pX = reader.ReadSingle();
				pY = reader.ReadSingle();
				pZ = reader.ReadSingle();
				Vector3 rightHandPosition = new Vector3(pX, pY, pZ);

				qX = reader.ReadSingle();
				qY = reader.ReadSingle();
				qZ = reader.ReadSingle();
				qW = reader.ReadSingle();
				Quaternion rightHandPose = new Quaternion(qX, qY, qZ, qW);

				clientDataList.Add(new ClientData(clientId, headPosition, headPose, leftHandPosition, leftHandPose, rightHandPosition, rightHandPose));
			}
			catch (Exception e) {
				break;
			}
		}

		return clientDataList;
	}

	public byte[] ToByteArray()
	{
		MemoryStream rawDataStream = new MemoryStream();
		BigEndianBinaryWriter dataWriter = new BigEndianBinaryWriter(rawDataStream);

		try {
			dataWriter.Write(id);

			dataWriter.Write(headPosition.x);
			dataWriter.Write(headPosition.y);
			dataWriter.Write(headPosition.z);
			dataWriter.Write(headPose.x);
			dataWriter.Write(headPose.y);
			dataWriter.Write(headPose.z);
			dataWriter.Write(headPose.w);

			dataWriter.Write(leftHandPosition.x);
			dataWriter.Write(leftHandPosition.y);
			dataWriter.Write(leftHandPosition.z);
			dataWriter.Write(leftHandPose.x);
			dataWriter.Write(leftHandPose.y);
			dataWriter.Write(leftHandPose.z);
			dataWriter.Write(leftHandPose.w);

			dataWriter.Write(rightHandPosition.x);
			dataWriter.Write(rightHandPosition.y);
			dataWriter.Write(rightHandPosition.z);
			dataWriter.Write(rightHandPose.x);
			dataWriter.Write(rightHandPose.y);
			dataWriter.Write(rightHandPose.z);
			dataWriter.Write(rightHandPose.w);
		}
		catch (Exception e) {
			Debug.Log(e.StackTrace);
		}
		return rawDataStream.ToArray();
	}

	public override string ToString()
	{
		string dataString = id.ToString() + headPosition.ToString() + headPose.ToString()
		+ leftHandPosition.ToString() + leftHandPose.ToString()
		+ rightHandPosition.ToString() + rightHandPose.ToString();
		return dataString;				
	}
}
