import java.net.InetAddress;

public class Utilisateur {
	private Personne who;
	private long idUtilisateur;

	public Utilisateur(long idU, InetAddress ip) {
		this.idUtilisateur = idU;
		who=new Personne(ip,"anonymous",true );
	}

	public Boolean setPseudo (String nouveauPseudo) {
		Boolean bool = true;
		if (Application.checkUnicity(nouveauPseudo)) {
			Application.sendPseudoSwitch(who.getPseudo(), nouveauPseudo, this.idUtilisateur);
			who.setPseudo(nouveauPseudo);
		} else {
			bool = false;
		}
		return bool;
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

	public long getId() {
		return this.idUtilisateur;
	}


}
