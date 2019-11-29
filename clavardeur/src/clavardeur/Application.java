package clavardeur;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Application implements Observer {
	Utilisateur user;
	HashMap<String,String> conv;
	VuePrincipale main;
	public static void main(String[] args) {
		
		Application monApp=new Application();
		monApp.show();
	}
	Application(){
		Enumeration<NetworkInterface> net= NetworkInterface.getNetworkInterfaces();//to work offline
		byte[] mac=net.nextElement().getHardwareAddress();
		user= new Utilisateur((mac.toString()).hashCode()); //fixe par poste (adresse mac by eg)
		conv=new HashMap<String,ConversationGui>;
		main=new VuePrincipale(this);
		main.show();
	}
	String getPseudo() {
		return user.getPseudo();
	}
	void sendActiveUserPseudo() {
		Reseau.getReseau().sendDataBroadcast(user.getPseudo().getBytes())
	}
	//before check unicty pseudo
	static void sendPseudoSwitch(String old, String newPseudo,long  id) {
		Reseau.getReseau().sendDataBroadcast("SWITCH".getBytes()+" "+old.getBytes()+" "+newPseudo.getBytes());
		BD maBd= BD.getBD();
		maBD.delIdPseudoLink(old);
		maBD.setIdPseudoLink(newPseudo,id);
	}
	boolean checkUnicity(String pseudo) {
		return BD.getBD().checkUnicity();
	}
	void deconnexion(String pseudo) {
		Reseau.getReseau().sendDataBroadcast("DECONNEXION".getBytes()+" "+pseudo.getBytes());
	}
	public createConversation(toPseudo) {
		conv.
	}
	@Override
	public void update(Observable o, Object arg) {
		//try convert arg to message 
		//if message => convGUI update
		//try string
		//si string and swithc
		//update pseudo
		//si pseudo ask
		//send UDP pseudo
		//si deconnexion, change status dans la liste des users
		//inactive sending in conversation
	}
