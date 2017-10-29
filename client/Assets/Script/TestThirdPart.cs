using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestThirdPart : MonoBehaviour {

	public ConnectionManager connectionManager;
	public RemotePlayerController remotePlayerController;

	public string remoteIp = "192.168.50.93";

	private ThirdPartManager thirdPartManager;

	public int remoteScore = 0;
	public float remoteBreathDegree = 0.0f;
	public float remoteBreathHeight = 0.0f;


	// Use this for initialization
	void Start () 
	{
		thirdPartManager = ThirdPartManager.Instance ;

	}

	void Update()
	{
		if (Input.GetKeyDown (KeyCode.Q)) {
			thirdPartManager.StartSend (remoteIp);
			Debug.Log ("Start send to third part.");
		}

		if (connectionManager != null && remotePlayerController != null) {
			thirdPartManager.UpdateLocalPlayerClientData (connectionManager.GetManager ().GetClientData ());

			RemotePlayer remotePlayer = remotePlayerController.GetRemotePlayerByIndex(0);
			thirdPartManager.UpdateRemotePlayerClientData (remoteScore, remoteBreathDegree, remoteBreathHeight, remotePlayer.Head.transform, remotePlayer.LeftHand.transform, remotePlayer.RightHand.transform);
		}
			
	}

	void OnApplicationQuit () 
	{
		thirdPartManager.StopSend ();
		Debug.Log ("UDP sender socket is closed.");
	}
}
