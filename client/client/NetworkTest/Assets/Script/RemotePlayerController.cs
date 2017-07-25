using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RemotePlayerController : MonoBehaviour, Listener {
	public GameObject Head;
	public GameObject LeftHand;
	public GameObject RightHand;
	private ClientData clientData;
	
	public void OnDataUpdated(Dictionary<byte, ClientData> clientDataMap, byte id)
	{
		byte key = (byte)(1 - id);
		if(clientDataMap.ContainsKey(key))
			clientData = clientDataMap [key];
	}

	void Start()
	{
		clientData = new ClientData ();
	}

	void Update()
	{
		Head.transform.position = clientData.headPosition;
		Head.transform.rotation = clientData.headPose;

		LeftHand.transform.position = clientData.leftHandPosition;
		LeftHand.transform.rotation = clientData.leftHandPose;

		RightHand.transform.position = clientData.rightHandPosition;
		RightHand.transform.rotation = clientData.rightHandPose;
	}
}
