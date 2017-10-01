using UnityEngine;


// TODO: No need to use this since status change can be handled in handler.
public class TestOnStatusChanged : MonoBehaviour, OnNewStatusChangedListener {

	// demo for implement OnNewStatusChanged(int newState)
	public void OnNewStatusChanged(int newState)
	{
		Debug.Log ("change to new state: " + newState.ToString ());
	}

}
