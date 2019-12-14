
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Reseau extends Observable implements Observer {
	ArrayList <Message> bufferReception;
	ServeurTCP reception;
	ClientTCP envoi;
	ArrayList <Message> bufferEnvoi;
	BroadcastClient clientUDP;
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
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
		     reception.closeServeur();
		    }});
		Thread tr = new Thread(reception);
        tr.start();
		this.serveurUDP = new ServeurUDP();
		this.serveurUDP.addObserver(this);
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
		     serveurUDP.closeServeur();
		    }});
		Thread tu = new Thread(serveurUDP);
        tu.start();
		this.envoi = new ClientTCP();
		this.clientUDP = new BroadcastClient();
		this.bufferReception = new ArrayList <Message>();
	}


	public static Reseau getReseau() throws IOException {
		if (theNetwork == null) {
			theNetwork = new Reseau();
		} 
		return theNetwork;
	}
	
	public void sendData(Message message) {
		try {
			envoi.sendMessage(message);
		} catch (IOException e) {
			//warning graphique envoi fail 
			e.printStackTrace();
		}
	}
	
	public void sendDataBroadcast(Message message) throws SocketException, IOException {
		clientUDP.broadcast(message);
	}
	public void getActiveUsers(Personne emmet) throws SocketException, IOException{
		Message message = new Message(Message.Type.WHOISALIVE, emmet);
		this.sendDataBroadcast(message);
	}
	public void update(Observable o, Object arg) {
		notifyObservers(arg);
	}
}
