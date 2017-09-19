using UnityEngine;

public class TestPairIdListener : MonoBehaviour, OnPairIdReceivedListener {

	// demo for implement OnPairIdReceived()
	public void OnPairIdReceived(int pairId)
	{
		Debug.Log ("pairId received: " + pairId.ToString ());
	}
}
