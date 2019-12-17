
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Observable;

public class ServeurUDP extends Observable implements Runnable{
	public final static int port = 1516;
	final static int taille = 1024;
	DatagramSocket socket = null;
	boolean on=true;

	/*@Override
	public void update(Observable o, Object arg) {
		System.out.print("\n ServeurUDP is notified ! (1st)");
		this.setChanged();
		notifyObservers(arg);
	}*/

	public void closeServeur() {
	   try {
		if(socket != null) {
		on=false;
		socket.close();
		System.out.print("Collected socket UDP ! (closed)");
		}
	   }catch (Exception e) {
	   e.printStackTrace();
	   }
	}

	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
				closeServeur();
		    }});

		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(on)
		{
			byte[] buffer = new byte[taille];
			DatagramPacket data = new DatagramPacket(buffer, buffer.length);

			try {
				socket.receive(data);
				byte[] myObject = new byte[data.getLength()];

				for(int i = 0; i < data.getLength(); i++)
				{
				     myObject[i] = buffer[i];
				}
				System.out.print("\n notify others !");
				System.out.print("\n Size : "+data.getLength());
				System.out.print("object :"+new String(myObject));
				setChanged();
				notifyObservers(Message.deserialize(myObject));
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
