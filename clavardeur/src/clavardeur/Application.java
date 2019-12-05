package clavardeur;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Application implements Observer {
	Utilisateur user;
	HashMap<String,ConversationGui> conv;
	VuePrincipale main;
	static BD maBD=BD.getBD();
	public static void main(String[] args) {
		
		Application monApp=new Application();
		monApp.show();
	}
	Application(){
		Enumeration<NetworkInterface> net= NetworkInterface.getNetworkInterfaces();//to work offline
		byte[] mac=net.nextElement().getHardwareAddress();
		user= new Utilisateur((mac.toString()).hashCode(),InetAddress.getLocalHost()); //fixe par poste (adresse mac by eg)
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
		maBD.delIdPseudoLink(old);
		maBD.setIdPseudoLink(newPseudo,id);
	}
	static boolean checkUnicity(String pseudo) {
		return maBD.checkUnicity();
	}
	void deconnexion(String pseudo) {
		Reseau.getReseau().sendDataBroadcast(new Message(message.Type.DECONNECTION,user.getPersonne());
	}
	public createConversation(Personne toPersonne) {
		ArrayList<Message> hist=maBD.getHistorique(maBD.getIdPersonne(toPersonne.getPseudo()));
		conv.put(toPersonne.getPseudo(), new ConversationGui(new Conversation(toPersonne,hist ),10));
	}
	@Override
	public void update(Observable o, Object arg) {
		//try convert arg to message 
		//if message => convGUI update
		  if (arg instanceof Message) {  
	           Message message = (Message) arg;  
	           if(message.getType()==message.Type.DEFAULT)
	        	   conv.get(message.getEmetteur()).update(message);
	           else if(message.getType()==message.Type.SWITCH) {
	        	   long id =maBD.getIdPersonne(message.getEmetteur);
	        	   maBD.delIdPseudoLink(message.getEmetteur);
	       		   maBD.setIdPseudoLink(message.getNewPseudo(),id);
	           }
	           else if(message.getType()==message.Type.DECONNECTION) {
	        	   conv.get(message.getEmetteur()).deconnection();
	           }
	           else if(message.getType()==message.Type.DECONNECTION) {
	        	   conv.get(message.getEmetteur()).deconnection();
	           }
	        	   
	        }  
		//try string
		//si string and swithc
		//update pseudo
		//si pseudo ask
		//send UDP pseudo
		//si deconnexion, change status dans la liste des users
		//inactive sending in conversation
	}
