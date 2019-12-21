
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

//https://www.baeldung.com/java-observer-pattern
//PropertyChangeListener better (java 11 )
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
	private Reseau() throws IOException {
		this.reception = new ServeurTCP();
		this.reception.addObserver(this);
		
		Thread tr = new Thread(reception);
        tr.start();
		this.serveurUDP = new ServeurUDP();
		this.serveurUDP.addObserver(this);
		Thread tu = new Thread(serveurUDP);
        tu.start();
        
		this.envoi = new ClientTCP();
		this.clientUDP = new ClientUDP();
		///ADD HOOK clientUDP destroy => sendBroadcast deconnexion
	}


	public static Reseau getReseau() throws IOException {
		if (theNetwork == null) {
			theNetwork = new Reseau();
		}
		return theNetwork;
	}
	public void sendTCP(Message message) {
		try {
			envoi.sendMessage(message);
		} catch (IOException e) {
			//warning graphique envoi fail
			e.printStackTrace();
		}
	}

	public void sendDataBroadcast(Message message) throws SocketException, IOException {
		System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi le message "+message.getType().toString()+" en broadcast ("+message.getDestinataire().getAdresse().toString()+")");
		clientUDP.broadcast(message);
	}

	public void sendUDP(Message message) throws SocketException, IOException {
		System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi d'un message "+message.getType().toString()+" à "+message.getDestinataire().getPseudo()+"("+message.getDestinataire().getAdresse().toString()+")");
		clientUDP.send(message);
	}  

	public void update(Observable o, Object arg) {
		this.setChanged();
		notifyObservers(arg);
	}
}
