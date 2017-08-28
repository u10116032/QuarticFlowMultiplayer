using System;
using System.Text;
using System.Collections.Generic;


public class StreamDataHandler : ResponseHandler{

    private Listener listener;

    public StreamDataHandler(Manager manager, Listener listener) : base(manager)
    {
        this.listener = listener;
    }

    public override void execute(byte[] contents)
    {
        List<ClientData> clientDataList = ClientData.Parse(contents);
        if (listener != null)
           listener.OnDataUpdated(clientDataList);
    }
}
