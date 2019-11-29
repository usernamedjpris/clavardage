package clavardeur;

public class Application {
	Utilisateur user;
	ConversationGui conv;
	VuePrinciapel main;
	
	public static void main(String[] args) {
		
		Application monApp=new Application();
		monApp.show();
	}
	Application(){
		user= new Utilisateur(1234); //fixe par poste
		
	}

}
