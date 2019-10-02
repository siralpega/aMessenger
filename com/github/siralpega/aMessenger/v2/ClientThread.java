package com.github.siralpega.aMessenger.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable
{
	private Socket client;

	protected String input, username;
	protected BufferedReader recieving;
	protected PrintWriter sending;

	public ClientThread(Socket cli)
	{
		//super("Client"); make better name (w/ num?)
		this.client = cli;
	}

	public void run()
	{
		try
		{
			//Setup I/O
			recieving = new BufferedReader(new InputStreamReader(client.getInputStream()));
			sending = new PrintWriter(client.getOutputStream(), true);

			//Get username	
			while(true)
			{
				sending.println("GETNAME");
				username = recieving.readLine();
				if(username == null)
					return;
				else if(username.isEmpty() || username.isBlank())
					return;
				else
					break;
			}

			Server.addName(username);
			sending.println("ADDED " + username);
			Server.addWriter(sending);

			while(true) 
			{
				String input = recieving.readLine();
				if (input.toLowerCase().startsWith("/quit"))
					break;

				Server.sendMessage(input, username);
			}

			if(sending != null && username != null)
			{
				Server.removeWriterAndName(sending, username);
				Server.sendMessage(username + " has left");
			}
			try { sending.close(); recieving.close(); client.close(); } catch(IOException ex) {System.out.println(ex.getMessage());}
		}
		catch(IOException ex)
		{
			System.out.println("IO Error: " + ex.getMessage());
			return;
		}	
	}
}
