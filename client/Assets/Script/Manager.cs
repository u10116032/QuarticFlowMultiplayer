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

	private static Manager Instance_;
	public static Manager Instance
	{
		get
		{
			if (Instance_ == null)
				Instance_ = new Manager ();
			return Instance_;
		}
	}

	private const string remoteIP = "140.112.31.113";
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
	private System.Object clientDataLock;

	private OnNetworkDataUpdatedListener onNetworkDataUpdatedListener;
	private OnPairIdReceivedListener onPairIdReceivedListener;
	private OnLoggedinListener onLoggedinListener;
	private OnDisconnectedListener onDisconnectedLinstener;
	private OnNewStatusChangedListener onNewStatusChangedListner;

	public void SetOnNetworkDataUpdatedListener(OnNetworkDataUpdatedListener onNetworkDataUpdatedListener)
	{
		this.onNetworkDataUpdatedListener = onNetworkDataUpdatedListener;
	}

	public void SetOnPairIdReceivedListener(OnPairIdReceivedListener onPairIdReceivedListener)
	{
		this.onPairIdReceivedListener = onPairIdReceivedListener;
	}

	public void SetOnLoggedinListener(OnLoggedinListener onLoggedinListener)
	{
		this.onLoggedinListener = onLoggedinListener;
	}

	public void SetOnDisconnectedListener(OnDisconnectedListener onDisconnectedLinstener)
	{
		this.onDisconnectedLinstener = onDisconnectedLinstener;
	}

	public void SetOnNewStatusChangedListener(OnNewStatusChangedListener onNewStatusChangedListner){
		this.onNewStatusChangedListner = onNewStatusChangedListner;
	}

	public void OnNetworkDataUpdated(List<ClientData> clientDataList){
		if(onNetworkDataUpdatedListener != null)
			onNetworkDataUpdatedListener.OnDataUpdated (clientDataList);
	}

	public void OnPairIdReceived(int pairId)
	{
		if (onPairIdReceivedListener != null)
			onPairIdReceivedListener.OnPairIdReceived (pairId);
	}

	public void OnLoggedin()
	{
		if (onLoggedinListener != null)
			onLoggedinListener.OnLoggedin ();
	}

	public void OnNewStatusChanged(int newStatus)
	{
		if (onNewStatusChangedListner != null)
			onNewStatusChangedListner.OnNewStatusChanged (newStatus);
	}

	private Manager ()
	{
        clientData = new ClientData();

        requestQueue = new Queue<byte[]>();

        requestMap = new Dictionary<String, ResponseHandler>();
        requestMap["SUCCESS"] = new SuccessHandler(this);
		requestMap["$"] = new StreamDataHandler(this);
		requestMap["PAIRID"] = new PairIdHandler(this);
		requestMap["NEWSTATUS"] = new NewStatusHandler (this);

        requestQueueLock = new System.Object();
        runningLock = new System.Object();
		clientDataLock = new System.Object ();
		SetRunning(false);
	}

	public void StartConnection(string remoteIp)
	{
		InitialConnect(remoteIp);

		sendThread = new Thread(Send);
		receiveThread = new Thread(Receive);

		sendThread.Start();
		receiveThread.Start();
	}

	public void Login(int id)
	{
		requestQueue.Enqueue(Encoding.UTF8.GetBytes("LOGIN " + id.ToString()));
    }

    public void StopConnection()
	{
        requestQueue.Enqueue(Encoding.UTF8.GetBytes("CLOSE"));
    }
		
	public void ChangeStatus(int status)
	{
		requestQueue.Enqueue(Encoding.UTF8.GetBytes("STATUS " + status.ToString()));
	}

	private void InitialConnect (string remoteIp) {
		Debug.Log("Attempt connection with server...");

		try {
			tcpClient = new TcpClient(remoteIp, serverPort);
		
			Debug.Log("Connected to server.");
            
            stream = tcpClient.GetStream();

			SetRunning(true);
        }
        catch (SocketException e) {
            // Connect fail.
            SetRunning(false);
			Debug.Log("SocketException: " + e.ErrorCode);
		}
	}

    private void Send()
    {
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
        while (IsRunning())
        {
            try {
                byte[] requestBytes = ReceiveResponse();
				if(requestBytes == null)
					continue;

                List<byte[]> tokenList = SplitResponseLine(requestBytes);

                string requestType = Encoding.UTF8.GetString(tokenList[0]);
                Debug.Log("receive response type: " + requestType);

                if (requestMap.ContainsKey(requestType))
                    requestMap[requestType].execute(tokenList[1 % tokenList.Count]);
            }
            catch (IOException e) {
                SetRunning(false);
                onDisconnectedLinstener.OnDisconnected();
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
			string requestType = Encoding.UTF8.GetString(requestByte);

            Array.Copy(requestByte, package, requestByte.Length);
            package[package.Length - 2] = Convert.ToByte('\r');
            package[package.Length - 1] = Convert.ToByte('\n');

            stream.Write(package, 0, package.Length);

			if (requestType == "CLOSE") {
                SetRunning(false);
                if (tcpClient != null) {
                    tcpClient.Close();
                    tcpClient = null;
                }
                Debug.Log("Disconnected.");
            }

            //stream.Flush();
			Debug.Log("Send: " + requestType);
        }
        catch (Exception e) {
            SetRunning(false);
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

    public void UpdateClientData(int score, float breathDegree, float breathHeight, Vector3 headPosition, Quaternion headPose, Vector3 leftHandPosition, Quaternion leftHandPose, Vector3 rightHandPosition, Quaternion rightHandPose)
    {
		lock (clientDataLock) {
			clientData.score = score;

			clientData.breathDegree = breathDegree;
			clientData.breathHeight = breathHeight;

			if (headPosition != null && headPose != null && leftHandPosition != null && leftHandPose != null && rightHandPosition != null && rightHandPose != null) {
				clientData.headPosition = headPosition;
				clientData.headPose = headPose;

				clientData.leftHandPosition = leftHandPosition;
				clientData.leftHandPose = leftHandPose;

				clientData.rightHandPosition = rightHandPosition;
				clientData.rightHandPose = rightHandPose;
			}
		}	
    }

    public ClientData GetClientData()
    {
		lock (clientDataLock) {
			return this.clientData;
		}
    }
}
	