using System.Collections.Generic;

public interface Listener {

	void OnDataUpdated(Dictionary<byte, ClientData> clientDataMap, byte id);

}
