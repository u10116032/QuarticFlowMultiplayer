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

	private UdpClient udpClient;
	private IPEndPoint remoteEndPoint;

	private ClientData localPlayerClientData;
	private ClientData remotePlayerClientData;

	private Thread SendThread;
	private bool isSending;

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

		isSending = false;

		this.localPlayerClientData = new ClientData ();
		this.remotePlayerClientData = new ClientData ();
	}

	public void UpdateLocalPlayerClientData(ClientData localPlayerClientData)
	{
		this.localPlayerClientData = localPlayerClientData;
	}

	public void UpdateRemotePlayerClientData(int remoteScore, float remoteBreathDegree, float remoteBreathHeight, Transform RemotePlayerHeadTransform, Transform RemotePlayerLeftHandTransform, Transform RemotePlayerRightHandTransform)
	{
		remotePlayerClientData.score = remoteScore;

		remotePlayerClientData.breathDegree = remoteBreathDegree;
		remotePlayerClientData.breathHeight = remoteBreathHeight;

		remotePlayerClientData.headPosition = RemotePlayerHeadTransform.localPosition;
		remotePlayerClientData.headPose = RemotePlayerHeadTransform.localRotation;

		remotePlayerClientData.leftHandPosition = RemotePlayerLeftHandTransform.localPosition;
		remotePlayerClientData.leftHandPose = RemotePlayerLeftHandTransform.localRotation;

		remotePlayerClientData.rightHandPosition = RemotePlayerRightHandTransform.localPosition;
		remotePlayerClientData.rightHandPose = RemotePlayerRightHandTransform.localRotation;
	}

	public void StartSend(string remoteIp)
	{
		if (isSending)
			return;
		
		isSending = true;

		remoteEndPoint = new IPEndPoint (IPAddress.Parse (remoteIp), 41000);
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

			byte[] playerDataBytes = localPlayerClientData.ToByteArray();
			byte[] otherPlayerDataBytes = remotePlayerClientData.ToByteArray();
			 
			byte[] packet = new byte[playerDataBytes.Length + otherPlayerDataBytes.Length + 1];
			packet [0] = (byte)currentStatus;

			Array.Copy (playerDataBytes, 0, packet, 1, playerDataBytes.Length);
			Array.Copy (otherPlayerDataBytes, 0, packet, 1 + playerDataBytes.Length, otherPlayerDataBytes.Length);

			udpClient.Send (packet, packet.Length, remoteEndPoint);
		}
	}

	public void SetCurrentStatus(int currentStatus)
	{
		this.currentStatus = currentStatus;
	}

}
