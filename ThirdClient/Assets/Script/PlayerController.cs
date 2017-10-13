using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerController : MonoBehaviour, OnDataUpdatedListener {

	public Player localPlayer;
	public Player remotePlayer;

	public void OnDataUpdated(List<ClientData> clientDataList)
	{
		if (clientDataList.Count != 0) 
		{
			localPlayer.clientData = clientDataList [0];
			remotePlayer.clientData = clientDataList [1];
		}
	}
	
	void Update () {

		if (localPlayer.clientData != null) {
			localPlayer.Head.transform.localPosition = localPlayer.clientData.headPosition;
			localPlayer.Head.transform.localRotation = localPlayer.clientData.headPose;

			localPlayer.LeftHand.transform.localPosition = localPlayer.clientData.leftHandPosition;
			localPlayer.LeftHand.transform.localRotation = localPlayer.clientData.leftHandPose;

			localPlayer.RightHand.transform.localPosition = localPlayer.clientData.rightHandPosition;
			localPlayer.RightHand.transform.localRotation = localPlayer.clientData.rightHandPose;
		}

		if (remotePlayer.clientData != null) {
			remotePlayer.Head.transform.localPosition = localPlayer.clientData.headPosition;
			remotePlayer.Head.transform.localRotation = localPlayer.clientData.headPose;

			remotePlayer.LeftHand.transform.localPosition = localPlayer.clientData.leftHandPosition;
			remotePlayer.LeftHand.transform.localRotation = localPlayer.clientData.leftHandPose;

			remotePlayer.RightHand.transform.localPosition = localPlayer.clientData.rightHandPosition;
			remotePlayer.RightHand.transform.localRotation = localPlayer.clientData.rightHandPose;
		}

	}

	public ClientData getLocalPlayerData()
	{
		return localPlayer.clientData;
	}

	public ClientData getRemotePlayerData()
	{
		return remotePlayer.clientData;
	}
}
