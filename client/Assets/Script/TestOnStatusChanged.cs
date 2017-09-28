using UnityEngine;

public class TestOnStatusChanged : MonoBehaviour {

    void Start()
    {
        // Instantiate delegate with lambda expression
        RemotePlayerController.OnStatusChanged onStatusChanged = newState => Debug.Log("Change State to " + newState);

        // set OnStateChanged per remote player by id
        RemotePlayerController remotePlayerController = GameObject.Find("RemotePlayer").GetComponent<RemotePlayerController>();   
        remotePlayerController.addOnStatusChangedMap(0, onStatusChanged);
    }

}
