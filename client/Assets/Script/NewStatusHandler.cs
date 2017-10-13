using System.Collections;
using System.Collections.Generic;


public class NewStatusHandler : ResponseHandler {

	public NewStatusHandler(Manager manager) : base(manager)
	{
		
	}

	public override void execute (byte[] contents)
	{
		int newStatus = contents[0];
		manager.OnNewStatusChanged (newStatus);

		ThirdPartManager thirdPartManager = ThirdPartManager.Instance;
		thirdPartManager.SetCurrentStatus (newStatus);
	}
}
