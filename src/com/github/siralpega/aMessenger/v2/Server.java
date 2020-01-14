package com.github.siralpega.aMessenger.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * TODO: If can't connect to server, then have client display that and let them re-enter
 * @author Alek
 */
public class Server 
{
	private static int port = 7777;
	private static List<PrintWriter> clients;
	private static List<String> names;
	private static boolean quitOnEmpty = false;

	public static void main(String[]args) throws IOException
	{
		ServerSocket server = null;
		ExecutorService pool = Executors.newCachedThreadPool();
		clients = new ArrayList<PrintWriter>();
		names = new ArrayList<String>();

		setup();

		try
		{
			server = new ServerSocket(port);	
		}
		catch(IOException ex)
		{
			System.out.println("ERROR: Couldn't create server on port " + port);
			System.exit(-1);
		}

		boolean run = true;
		while(run)
		{
			try 
			{
				Socket connectingClient = server.accept();
				pool.execute(new ClientThread(connectingClient));
			}
			catch (IOException ex)
			{
				System.out.println("ERROR: Couldn't connect client to server.");
				System.out.println(ex.getMessage());
			}
		}

		server.close();
	}

	private static void setup()
	{
		Scanner keyboard = new Scanner(System.in);

		System.out.println("SERVER AWAKE! Starting setup...");
		System.out.println("Enter port");
		port = keyboard.nextInt();
		System.out.println("Should server quit after last user quits? (yes/no)");
		String response = keyboard.next();
		if(response.equalsIgnoreCase("yes"))
			quitOnEmpty = true;
		System.out.println("Starting server on " + port);
		keyboard.close();
	}

	public static void sendMessage(String msg, String name)
	{
		System.out.println("SENDING FROM " + name + " : " + msg);
		for (PrintWriter pw : clients) 
			pw.println("MESSAGE " + name + ": " + msg);
	}

	public static void sendMessage(String msg)
	{
		for (PrintWriter pw : clients) 
			pw.println("MESSAGE " + msg);
	}
	
	public static void sendRawMessage(String msg)
	{
		for (PrintWriter pw : clients) 
			pw.println(msg);
	}

	public static void addName(String name)
	{
		System.out.println(">ADDING: " + name);
		names.add(name);
		for (PrintWriter pw : clients) 
			pw.println("MESSAGE " + ">> " + name + " has joined");
		sendRawMessage("LABEL " + getConnected());
	}

	public static void addWriter(PrintWriter pw)
	{
		clients.add(pw);
	}

	public static void removeWriterAndName(PrintWriter pw, String name)
	{
		System.out.println(">REMOVING: " + name);
		clients.remove(pw);
		names.remove(name);
		if(clients.isEmpty() && quitOnEmpty)
		{
			System.out.println(">!< Server is now empty, shutting down.");
			System.exit(0);
		}	
		sendRawMessage("LABEL " + getConnected());
	}
	
	public static String getConnected()
	{
		String x = "Connected Users (" + names.size() + "): ";
		for(int i = 0; i < names.size(); i++)
			x = x + names.get(i) + "  ";
		return x;	
	}
}
