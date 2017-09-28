using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System;

/// <summary>
/// Send : status breathDegree breathHeight Head LHand RHand [93byte]
/// Receive : id pairId status breathDegree breathHeight Head LHand RHand [95byte]
/// </summary>

public class ClientData {
	
	// @param id: account which is used to login the server
	// @param pairId: the pairId

	public byte id;
	public byte pairId{ get; set;}

	public byte status{ get; set;}
	public float breathDegree{ get;set;}
	public float breathHeight{ get; set;}

	public Vector3 headPosition { get; set;}
	public Quaternion headPose { get; set;}

	public Vector3 leftHandPosition { get; set;}
	public Quaternion leftHandPose { get; set;}

	public Vector3 rightHandPosition { get; set;}
	public Quaternion rightHandPose { get; set;}

	public ClientData()
	{
		status = (byte)0;

		breathDegree = 0.0f;
		breathHeight = 0.0f;

		headPosition = new Vector3 ();
		headPose = new Quaternion ();

		leftHandPosition = new Vector3 ();
		leftHandPose = new Quaternion ();

		rightHandPosition = new Vector3 ();
		rightHandPose = new Quaternion ();
	}
		
	public ClientData(byte id, byte pairId, byte status, float breathDegree, float breathHeight, Vector3 headPosition, Quaternion headPose, 
		Vector3 leftHandPosition, Quaternion leftHandPose, Vector3 rightHandPosition, Quaternion rightHandPose)
	{
		this.id = id;
		this.pairId = pairId;
		this.status = status;

		this.breathDegree = breathDegree;
		this.breathHeight = breathHeight;

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
				byte pairId = reader.ReadByte();

				byte status = reader.ReadByte();

				float breathDegree = reader.ReadSingle();
				float breathHeight = reader.ReadSingle();

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

				clientDataList.Add(new ClientData(clientId, pairId, status, breathDegree, breathHeight, headPosition, headPose, leftHandPosition, leftHandPose, rightHandPosition, rightHandPose));
			}
			catch (Exception e) {
				break;
			}
		}

		return clientDataList;
	}
		
	public byte[] ToByteArray()
	{
		MemoryStream rawDataStream = new MemoryStream(93);
		BigEndianBinaryWriter dataWriter = new BigEndianBinaryWriter(rawDataStream);

		try {
			dataWriter.Write(status);

			dataWriter.Write(breathDegree);
			dataWriter.Write(breathHeight);

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
		string dataString = id.ToString() + 
			pairId.ToString() + 
			status.ToString() + 
			breathDegree.ToString() +
			breathHeight.ToString() +
			headPosition.ToString() + 
			headPose.ToString() + 
			leftHandPosition.ToString() + 
			leftHandPose.ToString() + 
			rightHandPosition.ToString() + 
			rightHandPose.ToString();
		
		return dataString;				
	}
}
