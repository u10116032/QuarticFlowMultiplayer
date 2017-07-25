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
	private const int MAX_MESSAGE_LENGTH = 16;

	private string remoteIP = "127.0.0.1";
	private int serverPort = 40000;
	private int databasePort = 41000;
	private IPEndPoint remoteEndPoint;

	private TcpClient tcpClient;
	private StreamReader reader;
	private StreamWriter writer;

	private Thread connectThread;
	private bool isConnecting;
	private bool isSetup;
	private UdpClient udpClient;

	private Thread receiveThread;
	private Thread sendThread;
	private bool isStreaming;

	private Dictionary <byte, ClientData> clientDataMap;
	private ClientData clientData;

	public Listener listener;

	public Manager ()
	{
		remoteIP = "192.168.1.14";

		clientData = new ClientData ();
		clientDataMap = new Dictionary<byte, ClientData> ();
		remoteEndPoint = new IPEndPoint (IPAddress.Parse (remoteIP), databasePort);

		isConnecting = false;
		isSetup = false;
	}

	public void SetListener(Listener listener)
	{
		this.listener = listener;
	}

	public void StartConnection ()
	{
		if (!isConnecting) {
			connectThread = new Thread(Connect);
			connectThread.Start();
			isConnecting = true;
		}
	}

	public void StopConnection ()
	{
		if (isConnecting) {
			isConnecting = false;
			SendRequest("CLOSE");
		}
	}

	public void StartStream()
	{
		if (!isStreaming) {
			udpClient = new UdpClient(41000);

			receiveThread = new Thread(Receive);
			sendThread = new Thread(Send);

			receiveThread.Start();
			sendThread.Start();

			isStreaming = true;
		}
	}
	public void StopStream()
	{
		udpClient.Close ();
		isStreaming = false;
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
		Regex regex = new Regex (":");
		string[] tokens = regex.Split (message);
		if (tokens [0] == "id") {
			clientData.id = byte.Parse (tokens [1]);
			isSetup = true;
			StartStream ();
		}
			
		Debug.Log (message);	
	}

	public void Setup()
	{
		if(!isSetup)
			SendRequest ("SETUP");
	}

	private void Connect () {
		Debug.Log("Attempt connection with server...");

		while (isConnecting) {
			try {
				tcpClient = new TcpClient(remoteIP, serverPort);

				Debug.Log("Connected to server.");

				reader = new StreamReader(tcpClient.GetStream());
				writer = new StreamWriter(tcpClient.GetStream());

				while (true) {
					string message = reader.ReadLine();
					if (message == null)
						break;
					Debug.Log(message);
					ProcessMessage(message);
				}
			}
			catch (SocketException e) {
				// Connect fail.
				Debug.Log("SocketException: " + e.ErrorCode);
			}
		}

		if (tcpClient != null) {
			tcpClient.Close();
			tcpClient = null;
		}

		Debug.Log("Disconnected.");
	}

	private void Receive()
	{
		Debug.Log("Receiving stream data...");

		try {
			while (isStreaming) {
				IPEndPoint remoteIpEndPoint = new IPEndPoint(IPAddress.Any, 0);
				Byte[] receiveBytes = udpClient.Receive(ref remoteIpEndPoint);

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
			Debug.Log(e.StackTrace);
		}
	}

	private void Send()
	{
		Debug.Log("Sending stream data...");
		try {
			DateTime lastTime = DateTime.Now;
			while (isStreaming){
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

	/**
	* Get tcp keepalive setting.
	*
	* @param onOff	value to turn on(1) or off(0) keepalive.
	* @param keepAliveTime	timeout of idle to send keepalive, value in milliseconds.
	* @param keepAliveInterval	interval between successive keepalive packets.
	* @return optionInValue of IOControlCode.KeepAliveValues for Socket.IOControl.
	*/
	private byte[] GetKeepAliveSetting(int onOff, int keepAliveTime, int keepAliveInterval)
	{
		byte[] buffer = new byte[12];
		BitConverter.GetBytes(onOff).CopyTo(buffer, 0);
		BitConverter.GetBytes(keepAliveTime).CopyTo(buffer, 4);
		BitConverter.GetBytes(keepAliveInterval).CopyTo(buffer, 8);

		return buffer;
	}
}
