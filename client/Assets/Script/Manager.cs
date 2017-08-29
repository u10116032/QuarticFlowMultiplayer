using UnityEngine;
using System;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections.Generic;

public class Manager {
	private const string remoteIP = "127.0.0.1";
	private int serverPort = 40000;

	private TcpClient tcpClient;
    private Stream stream;

    private Thread sendThread;
    private Thread receiveThread;

    private bool running;
    private System.Object runningLock;

    private Queue<byte[]> requestQueue;
    private System.Object requestQueueLock;

    private Dictionary<String, ResponseHandler> requestMap;

    private ClientData clientData;

	public Manager (Listener listener)
	{
        clientData = new ClientData();

        requestQueue = new Queue<byte[]>();

        requestMap = new Dictionary<String, ResponseHandler>();
        requestMap["SUCCESS"] = new SuccessHandler(this);
        requestMap["$"] = new StreamDataHandler(this, listener);

        requestQueueLock = new System.Object();
        runningLock = new System.Object();

        running = true;

        sendThread = new Thread(Send);
        receiveThread = new Thread(Receive);

        InitialConnect();
        sendThread.Start();
        receiveThread.Start();
	}

	public void StartConnection()
	{
        requestQueue.Enqueue(Encoding.UTF8.GetBytes("LOGIN 3"));
    }

    public void StopConnection()
	{
        SetRunning(false);
        requestQueue.Enqueue(Encoding.UTF8.GetBytes("CLOSE"));

        if (tcpClient != null)
        {
            tcpClient.Close();
            tcpClient = null;
        }

        Debug.Log("Disconnected.");
    }
		
	private void InitialConnect () {
		Debug.Log("Attempt connection with server...");

		try {
			tcpClient = new TcpClient(remoteIP, serverPort);
		
			Debug.Log("Connected to server.");
            
            stream = tcpClient.GetStream();
        }
        catch (SocketException e) {
            // Connect fail.
            SetRunning(false);
			Debug.Log("SocketException: " + e.ErrorCode);
		}
	}

    private void Send()
    {
        Debug.Log("Sending stream data...");

        DateTime lastTime = DateTime.Now;
        while (IsRunning())
        {
            DateTime currentTime = DateTime.Now;

            byte[] request = GetRequest();
            if (request != null) {
                SendRequest(request);
                lastTime = currentTime;
            }

            if (currentTime.Subtract(lastTime).TotalMilliseconds > 250.0f)
            {
                SendRequest(new byte[1] { 0 });
                lastTime = currentTime;
            }
        }
    }

    private void Receive()
	{
		Debug.Log("Receiving stream data...");

        while (IsRunning())
        {
            try {
                byte[] requestBytes = ReceiveResponse();

                List<byte[]> tokenList = SplitResponseLine(requestBytes);

                string requestType = Encoding.UTF8.GetString(tokenList[0]);
                Debug.Log("receive response type: " + requestType);

                if (requestMap.ContainsKey(requestType))
                    requestMap[requestType].execute(tokenList[1 % tokenList.Count]);
            }
            catch (IOException e) {
                break;
            }
        }
    }

    private List<byte[]> SplitResponseLine(byte[] requestBytes)
    {
        List<byte[]> tokenList = new List<byte[]>();

        int delimIndex = 0;
        for (int i = 0; i < requestBytes.Length; ++i)
        {
            if (requestBytes[i] == ' ')
            {
                byte[] token = new byte[i - delimIndex];
                Array.Copy(requestBytes, delimIndex, token, 0, token.Length);
                tokenList.Add(token);
                delimIndex = i + 1;
                break;
            }
        }

        if (delimIndex != requestBytes.Length) {
            byte[] token = new byte[requestBytes.Length - delimIndex];
            Array.Copy(requestBytes, delimIndex, token, 0, token.Length);
            tokenList.Add(token);
        }
        return tokenList;
    }

    private void SendRequest(byte[] requestByte)
    {     
        try {
            byte[] package = new byte[requestByte.Length + 2];

            Array.Copy(requestByte, package, requestByte.Length);
            package[package.Length - 2] = Convert.ToByte('\r');
            package[package.Length - 1] = Convert.ToByte('\n');

            stream.Write(package, 0, package.Length);
            //stream.Flush();
            Debug.Log("Send: " + Encoding.UTF8.GetString(requestByte));
        }
        catch (Exception e) {
            Debug.Log(e.ToString());
        }
    }

    private byte[] ReceiveResponse() 
    {
        byte[] buffer = new byte[128];
        int bufferCount = 0;
        byte receivedByte;
        try
        {
            for (int i = 0; i < buffer.Length; ++i)
            {
                receivedByte = (byte)stream.ReadByte();
                if (receivedByte == '\r')
                {
                    receivedByte = (byte)stream.ReadByte();
                    if (receivedByte == '\n')
                    {
                        byte[] requestBytes = new byte[bufferCount];
                        Array.Copy(buffer, requestBytes, bufferCount);

                        return requestBytes;
                    }
                }
                buffer[bufferCount++] = receivedByte;
            }

            return null;
        }
        catch (IOException e) {
            throw e;
        }
    }

    public void AddRequest(byte[] request)
    {
        lock (requestQueueLock) {
            requestQueue.Enqueue(request);
        }
    }

    private byte[] GetRequest()
    {
        lock (requestQueueLock) {
            if (requestQueue.Count == 0)
                return null;
            try {
                
                return requestQueue.Dequeue();
            }
            catch (Exception e) {
                Debug.Log("Exception: " + e.StackTrace);
                return null;
            }
        }
    }

    public void SetRunning(bool running)
    {
        lock (runningLock) {
            this.running = running;
        }
    }

    public bool IsRunning()
    {
        lock (runningLock) {
            return running;
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

    public ClientData GetClientData()
    {
        return this.clientData;
    }


}
