
import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

//https://www.baeldung.com/java-observer-pattern
//PropertyChangeListener better (java 11 )
@SuppressWarnings("deprecation")
public class Reseau extends Observable implements Observer {
	//private PropertyChangeSupport support;
	ServeurTCP reception;
	ClientTCP envoi;
	ClientUDP clientUDP;
	ServeurUDP serveurUDP;
	static Reseau theNetwork;
	/**
	 * @param reception
	 * @param envoi
	 * @param clientUDP
	 * @throws IOException
	 */
	
	private Reseau() {
	}
	///TODO connexion à la classe gestion servlet
	void init(int portTCP, int portUDP, InetAddress ipLocale, InetAddress ipServer, int portServer) {
		this.reception = new ServeurTCP(portTCP);
		this.reception.addObserver(this);

		Thread tr = new Thread(reception);
        tr.start();
		this.serveurUDP = new ServeurUDP(portUDP,ipLocale);
		this.serveurUDP.addObserver(this);
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

	public void update(Observable o, Object arg) {
		this.setChanged();
		notifyObservers(arg);
	}
}
