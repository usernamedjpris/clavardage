package clavardeur;
import java.net.*;

public class Personne {
	private InetAddress adresse;
	private String pseudo;
	
	public Personne(InetAddress adresse, String pseudo) {
		this.adresse = adresse;
		this.pseudo = pseudo;
	}
}
