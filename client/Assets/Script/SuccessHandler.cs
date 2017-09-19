using System;
using System.Threading;
using System.IO;
using UnityEngine;

public class SuccessHandler : ResponseHandler {

	// Login but waiting for pairing, hence we can add a buttom to allow user cancel waiting
    
    public SuccessHandler(Manager manager) : base(manager)
    {
        
    }

    public override void execute(byte[] contents)
    {
        Debug.Log("Connected");
		manager.OnLoggedin ();
    }

}