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

	private Manager manager;
	private RemotePlayerController remotePlayerController;

	private Vector3 RemotePlayerHeadLocalPosition;
	private Quaternion RemotePlayerHeadLocalPose;

	private Vector3 RemotePlayerLeftHandPosition;
	private Quaternion RemotePlayerLeftHandPose;

	private Vector3 RemotePlayerRightHandPosition;
	private Quaternion RemotePlayerRightHandPose;

	private int remoteScore;
	private float remoteBreathDegree;
	private float remoteBreathHeight;

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
	}

	public void UpdateRemotePlayerTransform(int remoteScore, float remoteBreathDegree, float remoteBreathHeight, Transform RemotePlayerHeadTransform, Transform RemotePlayerLeftHandTransform, Transform RemotePlayerRightHandTransform)
	{
		this.remoteScore = remoteScore;

		this.remoteBreathDegree = remoteBreathDegree;
		this.remoteBreathHeight = remoteBreathHeight;

		this.RemotePlayerHeadLocalPosition = RemotePlayerHeadTransform.localPosition;
		this.RemotePlayerHeadLocalPose = RemotePlayerHeadTransform.localRotation;

		this.RemotePlayerLeftHandPosition = RemotePlayerLeftHandTransform.localPosition;
		this.RemotePlayerLeftHandPose = RemotePlayerLeftHandTransform.localRotation;

		this.RemotePlayerRightHandPosition = RemotePlayerRightHandTransform.localPosition;
		this.RemotePlayerRightHandPose = RemotePlayerRightHandTransform.localRotation;
	}

	public void StartSend(string remoteIp)
	{
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

			byte[] playerDataBytes = manager.GetClientData().ToByteArray();
			byte[] otherPlayerDataBytes;
			try{
				ClientData remotePlayerClientData = new ClientData();
				remotePlayerClientData.score = this.remoteScore;
				remotePlayerClientData.breathDegree = this.remoteBreathDegree;
				remotePlayerClientData.breathHeight = this.remoteBreathHeight;

				remotePlayerClientData.headPosition = RemotePlayerHeadLocalPosition;
				remotePlayerClientData.headPose = RemotePlayerHeadLocalPose;

				remotePlayerClientData.leftHandPosition = RemotePlayerLeftHandPosition;
				remotePlayerClientData.leftHandPose = RemotePlayerLeftHandPose;

				remotePlayerClientData.rightHandPosition = RemotePlayerRightHandPosition;
				remotePlayerClientData.rightHandPose = RemotePlayerRightHandPose;
					
				otherPlayerDataBytes = remotePlayerClientData.ToByteArray();
			}
			catch(NullReferenceException e){
				otherPlayerDataBytes = (new ClientData ()).ToByteArray();
			}
			 
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

	public void SetManager(Manager manager)
	{
		this.manager = manager;
	}

	public void SetRemotePlayerController(RemotePlayerController remotePlayerController)
	{
		this.remotePlayerController = remotePlayerController;
	}
}
