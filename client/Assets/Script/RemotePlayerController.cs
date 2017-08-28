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

	public void OnDataUpdated(List<ClientData> clientDataList)
	{
        Dictionary<int, ClientData> lastClientDataMap = new Dictionary<int, ClientData>(clientDataMap);
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
                clientDataMap.Remove(item.Value.id);
            }
        }

        foreach (ClientData currentClientData in clientDataList) {

            bool isContained = false;
            foreach (KeyValuePair<int, ClientData> item in clientDataMap) {
                if (currentClientData.id == item.Value.id) {
                    clientDataMap[item.Key] = currentClientData;
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
               
                List<int> indexList = new List<int>(clientDataMap.Keys);
                int index = 0;
                int listPointer = 0;
                while (true)
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

                clientDataMap.Add(index, currentClientData);
                
            }
        }

        Debug.Log("data update");
    }

	void Start()
	{
        clientDataList = new List<ClientData> ();
        clientDataMap = new Dictionary<int, ClientData>();   
    }

	void Update()
	{
        foreach (KeyValuePair<int, ClientData> item in clientDataMap) {
            int id = item.Key;
            if (id > remotePlayerList.Count - 1)
                continue;

            if (remotePlayerList[id] != null) {

                if (remotePlayerList[id].Head != null) { 
                    remotePlayerList[id].Head.transform.localPosition = clientDataMap[id].headPosition;
                    remotePlayerList[id].Head.transform.localRotation = clientDataMap[id].headPose;

                }

                if (remotePlayerList[id].LeftHand != null)
                {
                    remotePlayerList[id].LeftHand.transform.localPosition = clientDataMap[id].leftHandPosition;
                    remotePlayerList[id].LeftHand.transform.localRotation = clientDataMap[id].leftHandPose;

                }

                if (remotePlayerList[id].RightHand != null)
                {
                    remotePlayerList[id].RightHand.transform.localPosition = clientDataMap[id].rightHandPosition;
                    remotePlayerList[id].RightHand.transform.localRotation = clientDataMap[id].rightHandPose;

                }
            }
                
        }
	}
}
