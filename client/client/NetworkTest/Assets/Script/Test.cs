using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Test : MonoBehaviour {
	private Manager manager;
	public GameObject head;
	public GameObject leftHand;
	public GameObject rightHand;


	// Use this for initialization
	void Start () {
		manager = new Manager ();
		manager.SetListener (GameObject.Find ("RemotePlayer").GetComponent<RemotePlayerController> ());
	}
	
	// Update is called once per frame
	void Update () {
		if (Input.GetKey (KeyCode.A))
			manager.StartConnection ();
		if (Input.GetKey (KeyCode.S))
			manager.StopConnection ();
		if (Input.GetKey (KeyCode.D))
			manager.Setup ();
		manager.UpdateClientData (head.transform.position, head.transform.rotation, leftHand.transform.position, leftHand.transform.rotation, rightHand.transform.position, rightHand.transform.rotation);
	}

	void OnApplicationQuit()
	{
		manager.StopConnection ();
		manager.StopStream ();
	}

}
