
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

public class ServeurUDP extends Observable implements Runnable{
	ServerSocket ssoc = null;
	public final static int port = 1515;
	final static int taille = 1024;
	static byte buffer[] = new byte[taille];
	DatagramSocket socket = null;
	boolean on=true;

	@Override
	public void update(Observable o, Object arg) {
		System.out.print("\n ServeurUDP is notified ! (1st)");
		this.setChanged();
		notifyObservers(arg);
	}

	public void closeServeur() {
        try {
        	if(ssoc != null) {
        	on=false;
			ssoc.close();
			System.out.print("Collected socket UDP ! (closed)");
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
}
