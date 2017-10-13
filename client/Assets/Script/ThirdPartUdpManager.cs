using System;
using System.Net;
using System.Net.Sockets;

public class ThirdPartUdpManager  {

	private int port = 41000;

	private IPEndPoint remoteEndPoint;
	private UdpClient socket;

	public ThirdPartUdpManager()
	{
		remoteEndPoint = new IPEndPoint (IPAddress.Parse ("192.168.50.93"), port);
		socket = new UdpClient ();
	}

	public void Send(byte[] packet)
	{
		int length = packet.Length;
		socket.Send (packet, length, remoteEndPoint);
	}

	public byte[] Receive()
	{
		return socket.Receive (ref remoteEndPoint);
	}

}
