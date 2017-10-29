using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestThirdPart : MonoBehaviour {

	public PlayerController playerController;
	public TestOnStatusChanged testOnStatusChanged;

	private ThirdPartManager thirdPartManager;

	// Use this for initialization
	void Start () 
	{
		thirdPartManager = ThirdPartManager.Instance ;
		thirdPartManager.SetOnDataUpdatedListener (playerController);
		thirdPartManager.SetOnNewStatusChangedListener (testOnStatusChanged);
	}

	void Update()
	{
		if (Input.GetKeyDown (KeyCode.E)) {
			thirdPartManager.StartReceive ();
		}
			
	}

	// Update is called once per frame
	void OnApplicationQuit () 
	{
		thirdPartManager.StopReceive ();
	}
}
