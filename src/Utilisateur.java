import java.net.InetAddress;

public class Utilisateur {
	private Personne who;
	private long idUtilisateur;

	public Utilisateur(long idU, InetAddress ip) {
		this.idUtilisateur = idU;
		who=new Personne(ip,"anonymous",true );
	}

	public void setPseudo (String nouveauPseudo) {
			who.setPseudo(nouveauPseudo);
	}

	public String getPseudo() {
		return who.getPseudo();
	}

	public long getIdUtilisateur() {
		return idUtilisateur;
	}

	public Personne getPersonne() {
		return who;
	}

}
