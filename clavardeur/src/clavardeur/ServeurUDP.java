package clavardeur;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;

public class ServeurUDP extends Observable{
	public final static int port = 8532;
	final static int taille = 1024;
	static byte buffer[] = new byte[taille];
	
	public void launch() throws IOException {
		@SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket(port);
		while(true)
		{
			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			socket.receive(data);
			try {
				notifyObservers(Message.deserialize(data.getData()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			buffer="".getBytes();
		}
	}
}
