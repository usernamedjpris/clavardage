package clavardeur;
import java.util.HashMap;

public class Reseau immplements Observable {
	ServeurTCP reception;
	ClientTCP envoi;
	BroadcastClient clientUDP;
	static Reseau theNetwork;

	private Reseau() {
		this.reception = new ServeurTCP();
		this.envoi = new ClientTCP();
		this.clientUDP = new BroadcastClient();
	}

	public Reseau getReseau() {
		if (theNetwork == null) {
			theNetwork = new Reseau();
		} 
		return theNetwork;
	}
	
	public void sendData(Message message) {
		clientTCP
	}
	
	public Message getData() {
		
	}
	
	public 
}
