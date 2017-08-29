using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RemotePlayerController : MonoBehaviour, Listener {
	public GameObject Head;
	public GameObject LeftHand;
	public GameObject RightHand;

    public List<RemotePlayer> remotePlayerList = new List<RemotePlayer>();

	private List<ClientData> clientDataList;
    private Dictionary<int, ClientData> clientDataMap;
    private Dictionary<int, ClientData> clientDataMapBuffer;
    private System.Object clientDataMapLock;

    public void OnDataUpdated(List<ClientData> clientDataList)
	{
        clientDataMapBuffer = new Dictionary<int, ClientData>();
        lock (clientDataMapLock) {
            foreach (KeyValuePair<int, ClientData> item in clientDataMap) 
                clientDataMapBuffer.Add(item.Key, item.Value);
        }

        Dictionary<int, ClientData> lastClientDataMap = new Dictionary<int, ClientData>(clientDataMapBuffer);
        foreach (KeyValuePair<int, ClientData> item in lastClientDataMap) {
            int id = item.Value.id;

            bool isContained = false;
            foreach (ClientData clientData in clientDataList) {
                if (id == clientData.id) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
                clientDataMapBuffer.Remove(item.Key);
            }
        }

        foreach (ClientData currentClientData in clientDataList) {

            bool isContained = false;
            foreach (KeyValuePair<int, ClientData> item in clientDataMapBuffer) {
                if (currentClientData.id == item.Value.id) {
                    clientDataMapBuffer[item.Key] = currentClientData;
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
               
                List<int> indexList = new List<int>(clientDataMapBuffer.Keys);
                bool noKey = false;
                if (indexList.Count == 0)
                    noKey = true;
                indexList.Sort();
                int index = 0;
                int listPointer = 0;

                while (!noKey)
                {
                    if (index == indexList[listPointer])
                    {
                        index++;
                        listPointer++;

                        if (listPointer > indexList.Count - 1)
                            break;
                    }
                    else
                        break;
                }

                clientDataMapBuffer.Add(index, currentClientData);
                
            }
        }

        lock (clientDataMapLock) {
            clientDataMap = clientDataMapBuffer;
        }
    }

	void Start()
	{
        clientDataList = new List<ClientData> ();
        clientDataMap = new Dictionary<int, ClientData>();
        clientDataMapLock = new System.Object();
    }

	void Update()
	{
        lock (clientDataMapLock) { 
            foreach (KeyValuePair<int, ClientData> item in clientDataMap) {
                int id = item.Key;

                if (id > remotePlayerList.Count - 1)
                    continue;

                if (remotePlayerList[id] != null) {

                    if (remotePlayerList[id].Head != null) { 
                        remotePlayerList[id].Head.transform.localPosition = clientDataMap[id].headPosition;
                        remotePlayerList[id].Head.transform.localRotation = clientDataMap[id].headPose;
                    }

                    if (remotePlayerList[id].LeftHand != null) {
                        remotePlayerList[id].LeftHand.transform.localPosition = clientDataMap[id].leftHandPosition;
                        remotePlayerList[id].LeftHand.transform.localRotation = clientDataMap[id].leftHandPose;
                    }

                    if (remotePlayerList[id].RightHand != null) {
                        remotePlayerList[id].RightHand.transform.localPosition = clientDataMap[id].rightHandPosition;
                        remotePlayerList[id].RightHand.transform.localRotation = clientDataMap[id].rightHandPose;
                    }
                }
                
            }
        }
    }
}
