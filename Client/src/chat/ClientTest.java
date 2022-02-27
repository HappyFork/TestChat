package chat;

import javax.swing.JFrame;

public class ClientTest
{
	public static void main(String[] args)
	{
		Client client;
		client = new Client("XX.XXX.XXX.XX");
		client.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		client.startRunning();
	}
}
