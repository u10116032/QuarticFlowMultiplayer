using System;
using System.IO;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

public class ThirdPartManager {

	private int currentStatus = 0;

	private OnDataUpdatedListener onDataUpdatedListener;
	private OnNewStatusChangedListener onNewStatusChangedListener;

	private UdpClient udpClient;
	private IPEndPoint remoteEndPoint;

	private Thread ReceiveThread;
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

		isReceiving = false;
	}

	public void SetOnDataUpdatedListener(OnDataUpdatedListener onDataUpdatedListener)
	{
		this.onDataUpdatedListener = onDataUpdatedListener;
	}

	public void SetOnNewStatusChangedListener(OnNewStatusChangedListener onNewStatusChangedListener)
	{
		this.onNewStatusChangedListener = onNewStatusChangedListener;
	}

	public void StartReceive()
	{
		if (isReceiving)
			return;
		
		isReceiving = true;

		remoteEndPoint = new IPEndPoint (IPAddress.Any, 0);
		udpClient = new UdpClient (41000);

		ReceiveThread = new Thread (ReceiveTask);
		ReceiveThread.Start ();
	}

	public void StopReceive()
	{
		isReceiving = false;
		udpClient.Close ();
	}

	private void ReceiveTask()
	{
		while (isReceiving) {
			try{
				byte[] packet = udpClient.Receive (ref remoteEndPoint);
				int newStatus = Convert.ToInt32 (packet [0]);

				if (currentStatus != newStatus) {
					currentStatus = newStatus;
					if (onNewStatusChangedListener != null)
						onNewStatusChangedListener.OnNewStatusChanged (newStatus);
				}
					

				byte[] cropPacket = new byte[packet.Length - 1];
				Array.Copy (packet, 1, cropPacket, 0, packet.Length - 1);
				List<ClientData> clientDataList = Parse (cropPacket);

				if (onDataUpdatedListener != null)
					onDataUpdatedListener.OnDataUpdated (clientDataList);
			}
			catch(SocketException e){
				Debug.Log ("Socket is closed.");
			}
		}
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
}
