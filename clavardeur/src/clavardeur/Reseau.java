package clavardeur;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Reseau extends Observable implements Observer {
	ArrayList <Message> bufferReception;
	//ServeurTCP reception;
	ClientTCP envoi;
	ArrayList <Message> bufferEnvoi;
	BroadcastClient clientUDP;
	static Reseau theNetwork;
	/**
	 * @param reception
	 * @param envoi
	 * @param clientUDP
	 */
	private Reseau() {
		//this.reception = new ServeurTCP(this.getReseau());
		this.envoi = new ClientTCP();
		this.clientUDP = new BroadcastClient();
		this.bufferReception = new ArrayList <Message>();
	}

	public Reseau getReseau() {
		if (theNetwork == null) {
			theNetwork = new Reseau();
		} 
		return theNetwork;
	}
	
	public void sendMessage(String data, Personne dest, Personne emmet) throws IOException {
		envoi.sendMessage(data, dest, emmet);
	}
	
	public void sendDataBroadcast(String strMessage) throws SocketException, IOException {
		clientUDP.broadcast(strMessage);
	}
	
	public Message update(Observable o, Object arg) {
		notify
	}
}
