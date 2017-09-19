using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Test : MonoBehaviour {
	private Manager manager;

	public int status;
	public GameObject head;
	public GameObject leftHand;
	public GameObject rightHand;

	void Start ()
    {
		manager = Manager.Instance;
		manager.SetOnNetworkDataUpdatedListener (GameObject.Find("RemotePlayer").GetComponent<RemotePlayerController>());
		manager.SetOnPairIdReceivedListener (new TestPairIdListener());
		manager.SetOnLoggedinListener (new TestLogedinListener());
		manager.SetOnDisconnectedListener (new TestDisconnectedListener());
    }
	
	// Update is called once per frame
	void Update ()
    {
		if (Input.GetKeyDown(KeyCode.A)) 
            manager.StartConnection();
			
		if (Input.GetKeyDown(KeyCode.S))
			manager.StopConnection ();

		manager.UpdateClientData ((byte)status, head.transform.position, head.transform.rotation, leftHand.transform.position, leftHand.transform.rotation, rightHand.transform.position, rightHand.transform.rotation);
	}

	private IEnumerator ConnectTask()
	{
		Debug.Log("Start Task");
		int count = 0;
		while (true) {
			count++;
			Debug.Log("Test count: " + count);

			manager.StartConnection();
			yield return new WaitForSeconds(600);
			manager.StopConnection();
         
         int random = Random.Range(0, 600);
         Debug.Log("Wait for: " + random);
         yield return new WaitForSeconds(random);
      }
   }

	void OnApplicationQuit()
	{
		manager.StopConnection ();
	}

}
