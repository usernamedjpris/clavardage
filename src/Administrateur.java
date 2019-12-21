public class Administrateur  {
	BD bdd;
	public Administrateur() {
		this.bdd = BD.getBD();
	}
	
	public Boolean ajouterCompte(String pseudo) {
		Boolean isUnique = Application.checkUnicity(pseudo);
		if (isUnique) {
			//bdd.setIdPseudoLink(pseudo, adresse MAC)
		} 
		return isUnique;
	}

}
