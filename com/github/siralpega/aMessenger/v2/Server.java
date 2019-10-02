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
 * 
 * 
 * 
 * @author Alek
 * CREDIT: https://cs.lmu.edu/~ray/notes/javanetexamples/#chat
 */
public class Server 
{
	private static int port = 7777;
	private static List<PrintWriter> clients;
	private static List<String> names;

	public static void main(String[]args) throws IOException
	{
		ServerSocket server = null;
		ExecutorService pool = Executors.newCachedThreadPool();
		clients = new ArrayList<PrintWriter>();
		names = new ArrayList<String>();
		boolean run = true;

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
		System.out.println("Server has started.");
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

	public static void addName(String name)
	{
		System.out.println("ADDING: " + name);
		names.add(name);
		for (PrintWriter pw : clients) 
			pw.println("MESSAGE " + name + " has joined");

	}

	public static void addWriter(PrintWriter pw)
	{
		clients.add(pw);
	}

	public static void removeWriterAndName(PrintWriter pw, String name)
	{
		System.out.println("REMOVING: " + name);
		clients.remove(pw);
		names.remove(name);
	}
}
