using UnityEngine;
using System.Collections;
using System;
using System.Text.RegularExpressions;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections.Generic;

public class Manager {
	private string remoteIP = "140.112.31.113";
	private int serverPort = 40000;
	private int databasePort = 41000;
	private IPEndPoint remoteEndPoint;

	private TcpClient tcpClient;
	private StreamReader reader;
	private StreamWriter writer;

	private Thread connectThread;
	private bool connecting;
	private System.Object connectingLock;

	private UdpClient udpClient;

	private Thread receiveThread;
	private Thread sendThread;
	private bool streaming;
	private System.Object streamingLock;

	private Dictionary <byte, ClientData> clientDataMap;
	private ClientData clientData;

	public Listener listener;

	public Manager ()
	{
		remoteIP = "140.112.31.113";

		clientData = new ClientData ();
		clientDataMap = new Dictionary<byte, ClientData> ();
		remoteEndPoint = new IPEndPoint (IPAddress.Parse (remoteIP), databasePort);

		connectThread = null;
		connectingLock = new System.Object();
		SetConnecting(false);

		receiveThread = null;
		sendThread = null;
		streamingLock = new System.Object();
		SetStreaming(false);
	}

	public void SetListener(Listener listener)
	{
		this.listener = listener;
	}

	public bool StartConnection ()
	{
		if (!IsConnecting()) {
			if (connectThread != null) {
				connectThread.Join ();
				connectThread = null;
			}

			connectThread = new Thread(Connect);
			connectThread.Start();
			SetConnecting(true);

			return true;
		}

		return false;
	}

	public bool StopConnection ()
	{
		if (IsConnecting()) {
			SendRequest("CLOSE");
			SetConnecting(false);

			return true;
		}

		return false;
	}

	private void StartStream()
	{
		if (!IsStreaming()) {
			if (receiveThread != null) {
				receiveThread.Join ();
				receiveThread = null;
			}

			if (sendThread != null) {
				sendThread.Join ();
				sendThread = null;
			}

			udpClient = new UdpClient(databasePort);

			receiveThread = new Thread(Receive);
			sendThread = new Thread(Send);

			receiveThread.Start();
			sendThread.Start();

			SetStreaming(true);
		}
	}

	private void StopStream()
	{
		if (IsStreaming()) {
			udpClient.Close ();
			SetStreaming(false);
		}
	}

	private void SendRequest(string request) {
		try {
			writer.WriteLine(request);
			writer.Flush();
		}
		catch (Exception e) {
			Debug.Log(e.ToString());
		}
	}

	private void ProcessMessage(String message)
	{
		if (message == "ALIVE")
			return;
		else if (message == "ISALIVE") {
			SendRequest ("ALIVE");
			return;
		}

		Regex regex = new Regex (":");
		string[] tokens = regex.Split (message);
		if (tokens [0] == "id") {
			clientData.id = byte.Parse (tokens [1]);
			StartStream ();
		}
			
		Debug.Log (message);	
	}
		
	private void Connect () {
		Debug.Log("Attempt connection with server...");

		//while (isConnecting) {
		try {
			tcpClient = new TcpClient(remoteIP, serverPort);
		
			Debug.Log("Connected to server.");

			NetworkStream stream = tcpClient.GetStream();
			stream.ReadTimeout = 1000;
			reader = new StreamReader(tcpClient.GetStream());
			writer = new StreamWriter(tcpClient.GetStream());

			SendRequest ("SETUP");

			bool isAlive = true;
			while (IsConnecting()) {
				try {
					string message = reader.ReadLine();
					if (message == null)
						break;
					isAlive = true;
					ProcessMessage(message);
				}
				catch (IOException e) {
					if (isAlive) {
						Debug.Log("Keepalive");
						SendRequest("ISALIVE");
						isAlive = false;
					}
					else {
						Debug.Log("No response from server.");
						break;
					}
				}
			}
		}
		catch (SocketException e) {
			// Connect fail.
			Debug.Log("SocketException: " + e.ErrorCode);
		}


		StopStream ();

		if (tcpClient != null) {
			tcpClient.Close();
			tcpClient = null;
		}

		SetConnecting(false);
		Debug.Log("Disconnected.");
	}

	private void Receive()
	{
		Debug.Log("Receiving stream data...");

		try {
			while (IsStreaming()) {
				IPEndPoint receiveEndPoint = new IPEndPoint(IPAddress.Any, 0);
				Byte[] receiveBytes = udpClient.Receive(ref receiveEndPoint);
                
				List<ClientData> clientDataList = ClientData.Parse(receiveBytes);
				foreach(ClientData data in clientDataList) {
					if (data.id != clientData.id)
						clientDataMap[data.id] = data;
				}
				
				listener.OnDataUpdated(clientDataMap, clientData.id);
			}
		}
		catch (Exception e) {
			Debug.Log(e.ToString());
		}

		SetStreaming(false);
	}

	private void Send()
	{
		Debug.Log("Sending stream data...");
		try {
			DateTime lastTime = DateTime.Now;
			while (IsStreaming()){
				DateTime currentTime = DateTime.Now;
				if(currentTime.Subtract(lastTime).TotalMilliseconds < 20)
					continue;
				lastTime = currentTime;

				byte[] packet = clientData.ToByteArray();
				udpClient.Send(packet, packet.Length, remoteEndPoint);
			}
		}
		catch (Exception e) {
			Debug.Log(e.StackTrace);
		}

		SetStreaming(false);
	}

	public void UpdateClientData(Vector3 headPosition, Quaternion headPose, Vector3 leftHandPosition, Quaternion leftHandPose, Vector3 rightHandPosition, Quaternion rightHandPose)
	{
		clientData.headPosition = headPosition;
		clientData.headPose = headPose;
		clientData.leftHandPosition = leftHandPosition;
		clientData.leftHandPose = leftHandPose;
		clientData.rightHandPosition = rightHandPosition;
		clientData.rightHandPose = rightHandPose;
	}

	private bool IsConnecting()
	{
		lock (connectingLock) {
			return connecting;
		}
	}

	private void SetConnecting(bool connecting)
	{
		lock (connectingLock) {
			this.connecting = connecting;
		}
	}

	private bool IsStreaming()
	{
		lock (streamingLock) {
			return streaming;
		}
	}

	private void SetStreaming(bool streaming)
	{
		lock (streamingLock) {
			this.streaming = streaming;
		}
	}
}
