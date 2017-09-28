using System;
using System.Text;
using System.Collections.Generic;


public class StreamDataHandler : ResponseHandler{

	public StreamDataHandler(Manager manager) : base(manager)
    {
		
    }

    public override void execute(byte[] contents)
    {
        List<ClientData> clientDataList = ClientData.Parse(contents);

		manager.OnNetworkDataUpdated (clientDataList);
    }
}
