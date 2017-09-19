using UnityEngine;

public class TestDisconnectedListener : MonoBehaviour, OnDisconnectedListener {

	public void OnDisconnected()
	{
		Debug.Log("DisconnectedListener");
	}
}
