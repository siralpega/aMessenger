package com.github.siralpega.aMessenger.v2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client 
{
	private int port;
	private String ip;

	private BufferedReader recieving;
	private PrintWriter sending;
	private JFrame frame = new JFrame("aMessenger");
	private JTextArea messageBox = new JTextArea(16, 50);
	private JTextField textBox = new JTextField(50);

	private static String[] emoKeywords = {"oWo", "smile", "<3", "zzz"};
	private static int[] codePoints = {0x1F60B, 0x1F600, 0x2764, 0x1F634};

	public Client()
	{
		this.ip = getIP();
		this.port = Integer.parseInt(getPort());

		if(ip != null && !ip.isEmpty() && !ip.isBlank())
			ip = "localhost";
		if(port < 0)
			port = 7777;

		textBox.setEditable(false);
		messageBox.setEditable(false);
		frame.getContentPane().add(textBox, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageBox), BorderLayout.CENTER);
		frame.pack();

		// Send on enter then clear to prepare for next message
		textBox.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				sending.println(textBox.getText());
				textBox.setText("");
			}
		});

	}

	public static void main(String[]args) throws UnknownHostException, IOException
	{
		Client c = new Client();
		c.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.frame.setVisible(true);
		c.run();
	}

	private void run() throws IOException
	{
		Socket cs = new Socket(ip, port);
		recieving = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		sending = new PrintWriter(cs.getOutputStream(), true);

		while(true)
		{
			String input = recieving.readLine();

			if(input == null)
				break;
			if(input.startsWith("GETNAME"))
				sending.println(getName());
			else if(input.startsWith("ADDED"))
				textBox.setEditable(true);
			else if(input.startsWith("MESSAGE"))
			{
				for(int i = 0; i < emoKeywords.length; i++)
					if(input.contains(emoKeywords[i]))
					{
						char[] ar = Character.toChars(codePoints[i]);
						input = input.replaceAll(emoKeywords[i], new String(ar, 0, ar.length));
					}

				messageBox.append(input.substring(8) + "\n");
			}

		}
		frame.setVisible(false);
		frame.dispose();
		recieving.close();
		sending.close();
		cs.close();
	}	

	private String getName() {
		return JOptionPane.showInputDialog(
				frame,
				"Choose a screen name:",
				"Connect to a server",
				JOptionPane.PLAIN_MESSAGE
				);
	}

	private String getIP() {
		return JOptionPane.showInputDialog(
				frame,
				"Enter IP:",
				"Connect to a server",
				JOptionPane.PLAIN_MESSAGE
				);
	}

	private String getPort() {
		return JOptionPane.showInputDialog(
				frame,
				"Enter Port:",
				"Connect to a server",
				JOptionPane.PLAIN_MESSAGE
				);
	}
}
