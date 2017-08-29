import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*;

public class Server{
	private final int SERVER_PORT = 40000;
	private ServerSocket serverSocket;

 	private Server() throws Exception
 	{
		serverSocket = new ServerSocket(SERVER_PORT);
 	}

 	private void startServer ()
 	{
 		QFLogger.INSTANCE.Log("Server is starting...");

 		while (true) {
 			Socket socket = null;
			try {
 				socket = serverSocket.accept();

 				// Start service for new client. 				
 				ConnectionService connection = new ConnectionService(socket);
 			}
 			catch (IOException e) {
 				System.out.println("IOException : " + e.toString());
 			}
 		}
 	}
 	
	public static void main(String args[]) {
		Server server;
      	try {
	      	server = new Server();
	      	server.startServer();
 		}
 		catch (Exception e) {
 			System.out.println("Error opening server");
 			System.out.println("Exception :" + e.toString());
 		}
   }
}
