
import java.util.ArrayList;

public class Conversation {
	private Personne to;
	private ArrayList<Message> historique;
	/**
	 * @param to
	 * @param historique
	 */
	
	public Conversation(Personne to, ArrayList<Message> hist) {
		this.to = to;
		historique=hist;
	}
	
	public void AddMessage(ArrayList<Message> messages) {
		this.historique.addAll(messages);
	}

	public Personne getTo() {
		return to;
	}

	public ArrayList<Message> getHistorique() {
		return historique;
	}
}
