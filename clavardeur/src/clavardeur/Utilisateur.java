package clavardeur;

public class Utilisateur {
	private String pseudo;
	private long idUtilisateur;
	
	public Utilisateur(long idU) {
		this.idUtilisateur = idU;
		this.pseudo = "anonymous";
	}
	
	public Boolean setPseudo (String nouveauPseudo) {
		Boolean bool = true;
		if Application.checkUnicity(nouveauPseudo) {			
			Application.sendPseudoSwitch(this.pseudo, nouveauPseudo, this.idUtilisateur)
			this.pseudo = nouveauPseudo;
		} else {
			bool = false;
		}
		return bool;
	}

	public String getPseudo() {
		return pseudo;
	}

	public long getIdUtilisateur() {
		return idUtilisateur;
	}
	
	
}
