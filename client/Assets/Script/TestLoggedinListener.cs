using UnityEngine;
using System;

public class TestLogedinListener : MonoBehaviour, OnLoggedinListener  {

	// demo for implement OnLoggedin()
	public void OnLoggedin()
	{
		Debug.Log ("LoggedinListener");
	}
}
