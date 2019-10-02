package com.github.siralpega.twomessenger;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Foundation 
{
	protected static JFrame frame;
	protected static JPanel panel, menuPanel;
	protected static JButton sendMessage, endChat, menuEnter;
	protected static JTextField inputMessage, menuIP, menuPort, displayName;
	protected static JTextArea messageBox;
	protected static JLabel label, menuLabel;

	protected static PrintWriter sending;
	protected static BufferedReader recieving;
	protected static boolean running = false, clientReady = false;

	private static String username = "User", otherName = "Them", ip = "localhost";
	private static String[] emoKeywords = {"oWo", "smile", "<3", "zzz"};
	private static int[] codePoints = {0x1F60B, 0x1F600, 0x1F60D, 0x1F634};
	private static int port = 1234;
	private static ConnectionType type;

	//TODO:
	/* - Fix close button so that the instance that presses the button actually will close and get out of the while loop
	 * - Add scroll bar
	 * - Refresh clientSocket connection if a new connection is made 
	 * - make all of the client a thread, so u can have 2 or more!!!! clients connected together to a server instead of current server <-> client
	 *  use sync for thread to thread communication. example in lecture 3 && https://www.tutorialspoint.com/java/java_thread_communication.htm
	 */

	protected static Socket openConnection() throws UnknownHostException, IOException
	{
		Socket clientSocket;
		try
		{
			clientSocket = new Socket(ip, port);
		}
		catch(Exception ex)
		{
			return null; //maybe make label say "no connection established w/ server"
		}
		while(!clientReady)
			System.out.println("hello? client... wake up and type in a username");
		setupConnection(clientSocket);
		return clientSocket;
	}

	protected static Socket openConnection(ServerSocket serv) throws IOException
	{
		Socket clientSocket = serv.accept(); //TODO: need another check to make sure other client connected (aka passed menu panel)
		setupConnection(clientSocket);
		return clientSocket;
	}

	protected static void setupConnection(Socket client) throws IOException
	{
		sending = new PrintWriter(client.getOutputStream(), true);
		recieving = new BufferedReader(new InputStreamReader(client.getInputStream()));

		messageBox.append("CONNECTED! \n");
		sendMessage.setVisible(true);
		sending.println(username);
		otherName = recieving.readLine();
		running = true;
	}

	protected static void createGUI(ConnectionType connType)
	{
		type = connType;
		//Creating the Frame
		frame = new JFrame("aMessenger: " + type.toString());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		//Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("FILE");
		JMenu m2 = new JMenu("Help");
		mb.add(m1);
		mb.add(m2);
		JMenuItem m11 = new JMenuItem("Open");
		JMenuItem m22 = new JMenuItem("Save as");
		m1.add(m11);
		m1.add(m22);

		//Creating the panel at bottom and adding components
		panel = new JPanel(); // the panel is not visible in output
		label = new JLabel("Enter Text");
		inputMessage = new JTextField(10); // accepts up to 10 characters
		sendMessage = new JButton("Send");
		sendMessage.addActionListener(new ActionListener()
		{  
			public void actionPerformed(ActionEvent e)
			{  
				sendMessage(inputMessage.getText(), e.getSource());
			}  
		}); 
		endChat = new JButton("Close");
		endChat.addActionListener(new ActionListener()
		{  
			public void actionPerformed(ActionEvent e)
			{  
				try 
				{
					end();
				} 
				catch (IOException e1) 
				{
					System.out.println("ERROR: ending program. forcing close!");
					System.exit(0);
				}
			}  
		}); 
		panel.add(label); // Components Added using Flow Layout
		panel.add(label); // Components Added using Flow Layout
		panel.add(inputMessage);
		panel.add(sendMessage);
		panel.add(endChat);
		panel.setVisible(false);

		// Text Area at the Center
		messageBox = new JTextArea();
		messageBox.setEditable(false);
		messageBox.setFont(new Font(messageBox.getFont().toString(), Font.PLAIN, 16));

		//Create ip, port, and display name panel
		panel.setVisible(false);
		menuPanel = new JPanel(); // the panel is not visible in output
		menuLabel = new JLabel("Enter IP, port, and display name");
		menuIP = new JTextField(16); 
		menuPort = new JTextField(4);
		displayName = new JTextField(16);
		menuEnter = new JButton("Connect");
		menuEnter.addActionListener(new ActionListener()
		{  
			public void actionPerformed(ActionEvent e)
			{  
				try
				{
					establishConnection(menuIP.getText(), Integer.parseInt(menuPort.getText()), displayName.getText());
				}
				catch(Exception ex)
				{
					establishConnection(menuIP.getText(), port, displayName.getText());
				}
			}  
		}); 

		//Adding components to menuPanel
		menuPanel.add(menuLabel);
		menuPanel.add(menuIP);
		menuPanel.add(menuPort);
		menuPanel.add(displayName);
		menuPanel.add(menuEnter);

		//Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.add(menuPanel);
		frame.setVisible(true);
	}

	protected static void sendMessage(String msg, Object src)
	{ 
		sending.println(msg);
		for(int i = 0; i < emoKeywords.length; i++)
			if(msg.contains(emoKeywords[i]))
			{
				char[] ar = Character.toChars(codePoints[i]);
				msg = msg.replaceAll(emoKeywords[i], new String(ar, 0, ar.length));
			}
		messageBox.append(username + ": " + msg);
		messageBox.append("\n");
	}

	protected static void displayMessage(String msg)
	{
		for(int i = 0; i < emoKeywords.length; i++)
			if(msg.contains(emoKeywords[i]))
			{
				char[] ar = Character.toChars(codePoints[i]);
				msg = msg.replaceAll(emoKeywords[i], new String(ar, 0, ar.length));
			}
		messageBox.append(otherName + ": " + msg);
		messageBox.append("\n");
	}

	protected static void end() throws IOException
	{
		if(sending != null)
		{
			sending.println("end");
			recieving.close();
			sending.close();
		}
		frame.dispose();
	}

	protected static void establishConnection(String ipAd, int portAd, String name)
	{
		frame.remove(menuPanel);
		frame.getContentPane().add(BorderLayout.CENTER, messageBox);
		panel.setVisible(true);
		//TODO: set menu bar to setVisible true

		if(!ipAd.isEmpty() || !ipAd.isBlank())
			ip = ipAd;
		if(!name.isEmpty() || !name.isBlank())
			username = name; 
		port = portAd;

		label.setText("Enter Text");
		messageBox.append("Connecting to " + ip + " on port " + port + " as " + username + " \n");
		messageBox.append("Awaiting connection from client... \n");
		inputMessage.setText("");
		sendMessage.setVisible(false);
		
		for(int i = 0; i < emoKeywords.length; i++)
			;
		
		if(type == ConnectionType.CLIENT)
			clientReady = true;
	}
	
	protected static boolean isRunning()
	{
		return running;
	}

}
