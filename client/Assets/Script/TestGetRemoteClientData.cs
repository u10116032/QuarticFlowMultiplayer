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
		print("breathDegree: " + remotePlayerController.GetRemoteClientDataByIndex (0).breathDegree + ", breathHeight: " + remotePlayerController.GetRemoteClientDataByIndex (0).breathHeight);
	}
}
