using System.Threading;
using System;

public class PairIdHandler : ResponseHandler {

	Thread clientDataThread;

	public PairIdHandler(Manager manager) : base(manager)
	{
		clientDataThread = new Thread(clientDataTask);
	}

	public override void execute (byte[] contents)
	{
		int selfPairId = contents[0];
		manager.OnPairIdReceived (selfPairId);
		clientDataThread.Start();
	}

	private void clientDataTask()
	{
		DateTime lastTime = DateTime.Now;
		while (manager.IsRunning()) { 

			DateTime currentTime = DateTime.Now;
			if (currentTime.Subtract(lastTime).TotalMilliseconds < 33.0f)
				continue;
			lastTime = currentTime;

			byte[] clienDataBytes = manager.GetClientData().ToByteArray();
			byte[] request = new byte[clienDataBytes.Length + 2];
			request[0] = (byte)'$';
			request[1] = (byte)' ';
			Array.Copy(clienDataBytes, 0, request, 2, clienDataBytes.Length);

			manager.AddRequest(request);
		}
	}
}
