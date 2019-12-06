package clavardeur;
import java.util.ArrayList;

public class Conversation {
	private Personne to;
	private ArrayList<Message> historique;
	/**
	 * @param to
	 * @param historique
	 */
	public Conversation(Personne to) {
		this.to = to;
	}
	
	public void AddMessage(ArrayList<Message> messages) {
		this.historique.addAll(messages);
	}

	public Personne getTo() {
		return to;
	}
}
