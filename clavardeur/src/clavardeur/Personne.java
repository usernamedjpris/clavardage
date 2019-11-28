package clavardeur;
import java.net.*;

public class Personne {
	private InetAddress adresse;
	private long idPersonne;
	private String pseudo;
	
	public Personne(InetAddress adresse, long idPersonne, String pseudo) {
		this.adresse = adresse;
		this.idPersonne = idPersonne;
		this.pseudo = pseudo;
	}
}
