package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{
	
	private static final long serialVersionUID = 3393965466104804009L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client( String host )
	{
		super( "TestChat client window" );
		serverIP = host;
		System.out.println( serverIP );
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					sendMessage( e.getActionCommand() );
					userText.setText("");
				}
			}
		);
		
		add( userText, BorderLayout.SOUTH );
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add( new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize( 400, 300 );
		setVisible(true);
	}
	
	public void startRunning()
	{
		try
		{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException e1 )
		{
			showMessage( "\nClient terminated connection" );
		}catch( IOException e2 )
		{
			e2.printStackTrace();
		}
		finally
		{
			shutDown();
		}
	}
	
	private void connectToServer()
	{
		showMessage( "Attempting connection..." );
		try
		{
			connection = new Socket( InetAddress.getByName(serverIP), 3300 );
			showMessage( "Connected to:" + connection.getLocalAddress().getHostName() );
		} catch( IOException e5 )
		{
			showMessage( "\nUnable to connect." );
		}
	}
	
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream( connection.getOutputStream() );
		output.flush();
		input = new ObjectInputStream( connection.getInputStream() );
		showMessage( "\nStreams are now set up." );
	}
	
	private void whileChatting() throws IOException
	{
		ableToType(true);
		do{
			try
			{
				message = (String) input.readObject();
				showMessage( "\n" + message );
			}catch( ClassNotFoundException e3 )
			{
				showMessage( "\nMessage unreadable" );
			}
		}while( !message.equals( "SERVER - END" ));
	}
	
	private void shutDown()
	{
		showMessage( "\nShutting down..." );
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
	
	private void sendMessage( String message )
	{
		try
		{
			output.writeObject( "CLIENT - " + message );
			output.flush();
			showMessage( "\nCLIENT - " + message );
		}catch( IOException e5 )
		{
			chatWindow.append( "\nUnable to send message" );
		}
	}
	
	private void showMessage( final String text )
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
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
				public void run()
				{
					userText.setEditable( tof );
				}
			}
		);
	}
}
