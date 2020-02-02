
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Observable;
import com.clava.serializable.Message;
import com.clava.serializable.Personne;
public class ServeurUDP implements Runnable{
	private int port;
	final static int taille = 2048;
	DatagramSocket socket = null;
	boolean on=true;
	private PropertyChangeSupport support;
    /**
     * Ajoute un Listener à notifier (Reseau)
     * @param pcl
     * @see Reseau
     */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
	    support.addPropertyChangeListener(pcl);
	}
	/**
	 * Constructeur ServeurUDP
	 * <p>[Design Pattern Observers]</p>
	 * @param portUDP
	 */
	ServeurUDP(int portUDP){
		port=portUDP;
		support = new PropertyChangeSupport(this);
	}
	/**
	 * Permet de fermer en bonne et due forme le ServerSocket UDP et de libérer ainsi le port d'écoute pour la prochaine fois
	 */
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
	/**
	 * Remonte le message deserializé au Listener (Reseau)
	 */
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
				support.firePropertyChange("message","", Message.deserialize(myObject));
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
