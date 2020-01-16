
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import com.clava.serializable.Message;
import com.clava.serializable.Personne;

//https://www.baeldung.com/java-observer-pattern
//PropertyChangeListener better (java 11 )

@SuppressWarnings("deprecation")
//public class Reseau extends Observable implements Observer {
public class Reseau implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private ServeurTCP reception;
	private ClientTCP envoi;
	private ClientUDP clientUDP;
	private ServeurUDP serveurUDP;
	static Reseau theNetwork;
	/**
	 * @param reception
	 * @param envoi
	 * @param clientUDP
	 * @throws IOException
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
	private Reseau() {
	}
	///TODO connexion à la classe gestion servlet
	void init(int portTCP, int portUDP, InetAddress ipServer, int portServer) {
		support = new PropertyChangeSupport(this);
		this.reception = new ServeurTCP(portTCP);
		this.reception.addPropertyChangeListener(this);

		Thread tr = new Thread(reception);
        tr.start();
		this.serveurUDP = new ServeurUDP(portUDP);
		this.serveurUDP.addPropertyChangeListener(this);
		Thread tu = new Thread(serveurUDP);
        tu.start();

		this.envoi = new ClientTCP();//on get auto adresse +port dans personne destinataire (get from serveur/UDP #discovery part)
		this.clientUDP = new ClientUDP(portUDP);//port nécessaire pour broadcast, #same config UDP everywhere
	}

	public static Reseau getReseau() {
		if (theNetwork == null) {
				theNetwork = new Reseau();
		}
		return theNetwork;
	}
	public void sendTCP(Message message) {
		try {
			System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi le message "+message.getType().toString()+" en tcp ("
		+message.getDestinataire().getAdresse().toString()+") port :"+message.getDestinataire().getPort());
			envoi.sendMessage(message);
		} catch (IOException e) {
			//warning graphique envoi fail
			JOptionPane.showMessageDialog(null, "Erreur réseau à l'envoi du message :'( ", "Erreur ", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}
	}

	public void sendDataBroadcast(Message message) {
	try {
			clientUDP.broadcast(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendUDP(Message message) {
		System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi d'un message "+message.getType().toString()+" à "+
	message.getDestinataire().getPseudo()+"("+message.getDestinataire().getAdresse().toString()+") port "+
	message.getDestinataire().getPort());
		try {
			clientUDP.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange("message", evt.getOldValue(), evt.getNewValue());
	}
}
