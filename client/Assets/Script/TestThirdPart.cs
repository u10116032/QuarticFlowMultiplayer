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
			thirdPartManager.SetManager (connectionManager.GetManager());
			thirdPartManager.SetRemotePlayerController (remotePlayerController);

			thirdPartManager.StartSend (remoteIp);
		}

		RemotePlayer remotePlayer = remotePlayerController.GetRemotePlayerByIndex(0);
		thirdPartManager.UpdateRemotePlayerTransform (remoteScore, remoteBreathDegree, remoteBreathHeight, remotePlayer.Head.transform, remotePlayer.LeftHand.transform, remotePlayer.RightHand.transform);
			
	}

	// Update is called once per frame
	void OnDestroy () 
	{
		thirdPartManager.StopSend ();
	}
}
