using System;
using System.Threading;

public class ThirdPartManager {

	private int status = 0;

	private Manager manager;
	private RemotePlayerController remotePlayerController;

	private ThirdPartUdpManager udpManager;

	private Thread SendThread;
	private Thread ReceiveThread;

	private bool isSending;
	private bool isReceiving;

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

	public ThirdPartManager(Manager manager, RemotePlayerController remotePlayerController)
	{
		this.udpManager = new ThirdPartUdpManager ();
		this.manager = manager;
		this.remotePlayerController = remotePlayerController;
	}

	public void StartSend()
	{
		isSending = true;
		SendThread = new Thread (SendTask);
		SendThread.Start ();
	}

	public void StopSend()
	{
		isSending = false;
	}

	private void SendTask()
	{
		while (isSending) {
			byte[] playerDataBytes = manager.GetClientData().ToByteArray();
			byte[] otherPlayerDataBytes = remotePlayerController.GetRemoteClientDataByIndex (0).ToByteArray();

			byte[] packet = new byte[playerDataBytes.Length + otherPlayerDataBytes.Length + 1];
			packet [0] = (byte)status;

			Array.Copy (playerDataBytes, 0, packet, 1, playerDataBytes.Length);
			Array.Copy (otherPlayerDataBytes, 0, packet, 1 + playerDataBytes.Length, otherPlayerDataBytes.Length);

			udpManager.Send (packet);
		}
	}

	public void StartReceive()
	{
		isReceiving = true;
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
			byte[] packet = udpManager.Receive ();
			// TODO: parse packet
		}
	}

	public void SetStatus()
	{
		this.status = status;
	}

}
