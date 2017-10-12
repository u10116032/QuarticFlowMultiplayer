using System;
using System.Net;
using System.Net.Sockets;

public class ThirdPartUdpManager  {

	private int port = 41000;

	private IPEndPoint remoteEndPoint;
	private UdpClient socket;

	public ThirdPartUdpManager()
	{
		remoteEndPoint = new IPEndPoint (IPAddress.Parse ("127.0.0.1"), port);
		socket = new UdpClient ();
	}

	public void Send(byte[] packet)
	{
		socket.Send (packet, packet.Length, remoteEndPoint);
	}

	public byte[] Receive()
	{
		return socket.Receive (ref remoteEndPoint);
	}

}
