package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Server extends JFrame
{
	private static final long serialVersionUID = -6854881916960018778L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//Constructor
	public Server()
	{
		super( "Some bullshit text, yo" );
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener()
				{
					public void actionPerformed( ActionEvent event )
					{
						sendMessage( event.getActionCommand() );
						userText.setText( "" );
					}
				});
		add( userText, BorderLayout.SOUTH );
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add( new JScrollPane( chatWindow ) );
		setSize(400,300);
		setVisible(true);
	}
	
	//Setup and run
	public void start()
	{
		try
		{
			server = new ServerSocket( 3300 );
			while(true)
			{
				try
				{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException e2)
				{
					showMessage( "\nServer ended the connection!" );
				}finally
				{
					shutDown();
				}
			}
		} catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
	
	//wait for connection, then display connection info
	private void waitForConnection() throws IOException
	{
		showMessage( "Waiting for someone to connect..." );
		connection = server.accept();
		showMessage( "Connected to " + connection.getLocalAddress().getHostName() );
	}
	
	//setup streams to send and receive data
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream( connection.getOutputStream() );
		output.flush();
		input = new ObjectInputStream( connection.getInputStream() );
		showMessage( "\nStreams are now set up." );
	}
	
	//runs during conversation
	private void whileChatting() throws IOException
	{
		String message = "You are now connected.";
		sendMessage( message );
		ableToType(true);
		do
		{
			try
			{
				message = (String) input.readObject();
				showMessage( "\n" + message );
			}catch( ClassNotFoundException e3 )
			{
				showMessage( "\nMessage unreadable." );
			}
		}while( !message.equals("CLIENT - END") );
	}
	
	//closes everything
	private void shutDown()
	{
		showMessage( "\nClosing connections..." );
		ableToType(false);
		try
		{
			output.close();
			input.close();
			connection.close();
		}catch( IOException e4 )
		{
			e4.printStackTrace();
		}
	}
	
	//send a message to the client
	private void sendMessage( String message )
	{
		try
		{
			output.writeObject( "SERVER - " + message );
			output.flush();
			showMessage( "\nSERVER - " + message );
		}catch( IOException e5 )
		{
			chatWindow.append( "\nERROR: Unable to send message" );
		}
	}
	
	//Updates chat window
	private void showMessage( final String text )
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run(){
					chatWindow.append( text );
				}
			}
		);
	}
	
	private void ableToType( final boolean tof )
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run(){
					userText.setEditable( tof );
				}
			}
		);
	}
}
