using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestGetClientData : MonoBehaviour {

	public PlayerController playerController;

	void Update()
	{
		if(playerController.getLocalPlayerData () != null)
			Debug.Log ("local breathDegree: " + playerController.getLocalPlayerData ().breathDegree);

		if( playerController.getRemotePlayerData () != null)
			Debug.Log ("remote breathDegree: " + playerController.getRemotePlayerData ().breathDegree);
	}


}
