using System;
using System.IO;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

public class ThirdPartManager {

	private int status = 0;

	private UdpClient udpClient;
	private IPEndPoint remoteEndPoint;

	private Manager manager;
	private RemotePlayerController remotePlayerController;

	private Thread SendThread;
	private Thread ReceiveThread;

	private bool isSending;
	private bool isReceiving;

	private DateTime lastTime;

	private static ThirdPartManager Instance_;
	public static ThirdPartManager Instance
	{
		get
		{
			if (Instance_ == null)
				Instance_ = new ThirdPartManager ();

			return Instance_;
		}
	}

	private ThirdPartManager()
	{
		lastTime = DateTime.Now;
	}

	public void StartSend()
	{
		isSending = true;

		remoteEndPoint = new IPEndPoint (IPAddress.Parse ("192.168.50.93"), 41000);
		udpClient = new UdpClient ();

		SendThread = new Thread (SendTask);
		SendThread.Start ();
	}

	public void StopSend()
	{
		isSending = false;
	}

	private void SendTask()
	{
		// send: status playerByte otherPlayerByte
		while (isSending) {

			DateTime currentTime = DateTime.Now;
			if (currentTime.Subtract(lastTime).TotalMilliseconds < 33.0f)
				continue;
			lastTime = currentTime;

			byte[] playerDataBytes = manager.GetClientData().ToByteArray();
			byte[] otherPlayerDataBytes;
			try{
				otherPlayerDataBytes = remotePlayerController.GetRemoteClientDataByIndex (0).ToByteArray();
			}
			catch(NullReferenceException e){
				otherPlayerDataBytes = (new ClientData ()).ToByteArray();
			}
			 
			byte[] packet = new byte[playerDataBytes.Length + otherPlayerDataBytes.Length + 1];
			packet [0] = (byte)status;

			Array.Copy (playerDataBytes, 0, packet, 1, playerDataBytes.Length);
			Array.Copy (otherPlayerDataBytes, 0, packet, 1 + playerDataBytes.Length, otherPlayerDataBytes.Length);

			udpClient.Send (packet, packet.Length, remoteEndPoint);
		}
	}

	public void StartReceive()
	{
		isReceiving = true;

		remoteEndPoint = new IPEndPoint (IPAddress.Any, 0);
		udpClient = new UdpClient (41000);

		ReceiveThread = new Thread (ReceiveTask);
		ReceiveThread.Start ();
	}

	public void StopReceive()
	{
		isReceiving = false;
	}

	private void ReceiveTask()
	{
		while (isReceiving) {
			byte[] packet = udpClient.Receive (ref remoteEndPoint);
			int newStatus = Convert.ToInt32 (packet [0]);

			byte[] cropPacket = new byte[packet.Length - 1];
			Array.Copy (packet, 1, cropPacket, 0, packet.Length - 1);
			List<ClientData> clientDataList = Parse (cropPacket);


		}
	}

	public void SetStatus(int status)
	{
		this.status = status;
	}

	public List<ClientData> Parse(byte[] rawData)
	{
		List<ClientData> clientDataList = new List<ClientData>();

		MemoryStream stream = new MemoryStream(rawData);
		BigEndianBinaryReader reader = new BigEndianBinaryReader(stream);

		while (true) {
			try {

				int score = reader.ReadInt32();

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

				clientDataList.Add(new ClientData((byte)0, (byte)0, score, breathDegree, breathHeight, headPosition, headPose, leftHandPosition, leftHandPose, rightHandPosition, rightHandPose));
			}
			catch (Exception e) {
				break;
			}
		}

		return clientDataList;
	}


	public void SetManager(Manager manager)
	{
		this.manager = manager;
	}

	public void SetRemotePlayerController(RemotePlayerController remotePlayerController)
	{
		this.remotePlayerController = remotePlayerController;
	}
}
