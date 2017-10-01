using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestGetRemoteClientData : MonoBehaviour {

	RemotePlayerController remotePlayerController;
	// Use this for initialization
	void Start () {
		remotePlayerController = GameObject.Find("RemotePlayer").GetComponent<RemotePlayerController>();   
	}
	
	// Update is called once per frame
	void Update () {
		ClientData remoteClientData = remotePlayerController.GetRemoteClientDataByIndex (0);
		if (remoteClientData != null) {
			print ("breathDegree: " + remoteClientData.breathDegree + ", breathHeight: " + remoteClientData.breathHeight);
			print ("score: " + remoteClientData.score);
		}
	}
}
