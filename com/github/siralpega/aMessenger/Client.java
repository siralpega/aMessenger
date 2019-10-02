package com.github.siralpega.twomessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Foundation
{
	public static void main(String[]args) throws UnknownHostException, IOException
	{
		createGUI(ConnectionType.CLIENT);
		Socket clientSocket = openConnection();
		if(clientSocket == null)
		{
			System.out.println("clientSocket returned null. that means no server is running. so we should tell the client, then let them retry or exit program");
			System.exit(0);
		}


		String input;
		while(isRunning())
		{
			input = recieving.readLine();
			if(input == null)
				continue;
			else if(input.equalsIgnoreCase("end"))
			{
				running = false;
				end();
			}
			else
				displayMessage(input);	
		} 

		clientSocket.close();
		System.exit(0);	
	}
}
