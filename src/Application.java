
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Application implements Observer {
	static Utilisateur user;
	HashMap<String,ConversationGui> conv;
	VuePrincipale main;
	static BD maBD=BD.getBD();
	ArrayList<Personne> pActives=new ArrayList<Personne>();
	ArrayList<String> pInactives=new ArrayList<String>();
	public static void main(String[] args) {
		Application monApp=new Application();
	}
	Application(){
		Enumeration<NetworkInterface> net= NetworkInterface.getNetworkInterfaces();//to work offline
		byte[] mac=net.nextElement().getHardwareAddress();
		user= new Utilisateur((mac.toString()).hashCode(),InetAddress.getLocalHost()); //fixe par poste (adresse mac by eg)
		Reseau.getReseau().getActiveUsers(user.getPersonne());
		Reseau.getReseau().addObserver(this);
		conv=new HashMap<String,ConversationGui>;
		main=new VuePrincipale(this);
	}
	String getPseudo() {
		return user.getPseudo();
	}
	void sendActiveUserPseudo() {
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.ALIVE,user.getPersonne()));
	}
	//after check unicty pseudo
	static void sendPseudoSwitch(String old, String newPseudo,long  id) {
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.SWITCH,user.getPersonne(),newPseudo));
		maBD.delIdPseudoLink(old);
		maBD.setIdPseudoLink(newPseudo,id);
	}
	static boolean checkUnicity(String pseudo) {
		return maBD.checkUnicity();
	}
	void deconnexion(String pseudo) {
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.DECONNECTION,user.getPersonne()));
	}
	public void createConversation(Personne toPersonne) {
		ArrayList<Message> hist=maBD.getHistorique(maBD.getIdPersonne(toPersonne.getPseudo()));
		conv.put(toPersonne.getPseudo(), new ConversationGui(new Conversation(toPersonne,hist),10));
	}
	public ArrayList<Personne> getpActives() {
		return pActives;
	}
	public ArrayList<String> getPseudoTalked(){
		maBD.getPseudoTalked(user.getId());
	}
	@Override
public void update(Observable o, Object arg) {
		//try convert arg to message 
		//if message => convGUI update
		  if (arg instanceof Message) {  
	           Message message = (Message) arg;  
	           if(message.getType()==Message.Type.DEFAULT)
	        	   conv.get(message.getEmetteur()).update(message);
	           else if(message.getType()==Message.Type.SWITCH) {
	        	   long id =maBD.getIdPersonne(message.getEmetteur());
	        	   maBD.delIdPseudoLink(message.getEmetteur());
	       		   maBD.setIdPseudoLink(message.getNewPseudo(),id);
	           }
	           else if(message.getType()==Message.Type.DECONNECTION) {
	        	   conv.get(message.getEmetteur()).deconnection();
	           }
	           else if(message.getType()==Message.Type.ALIVE) {
	        	   //show status bar
	        	   
	           }
	           else if(message.getType()==Message.Type.WHOISALIVE) {
	        	   sendActiveUserPseudo();
	           }
	           else if(message.getType()==Message.Type.CONNECTION) {
	        	   pActives.add(message.emetteur);
	        	   //show pop-up
	           }
	           else
	        	   System.out.print("WARNING unknow message type !");
	        	   
	        }  
		//try string
		//si string and swithc
		//update pseudo
		//si pseudo ask
		//send UDP pseudo
		//si deconnexion, change status dans la liste des users
		//inactive sending in conversation
	}
}