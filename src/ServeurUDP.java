
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Observable;

public class ServeurUDP extends Observable implements Runnable{
	public final static int port = 8534;
	final static int taille = 1024;
	static byte buffer[] = new byte[taille];
	DatagramSocket socket = null;
	boolean on=true;
	@Override
	public void run() {
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(on)
		{
			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(data);
				notifyObservers(Message.deserialize(data.getData()));
			} catch (IOException e1) {
				if(on)
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				if(on)
				e.printStackTrace();
			}
			buffer="".getBytes();
		}
		
	}


	public void closeServeur() {
		on=false;
		if(socket != null) {
		socket.close();
		System.out.print("Collected socket UDP ! (closed) \n");
		}
	}
}
