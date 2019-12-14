
import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

public class Application implements Observer {
	static Utilisateur user;
	//
	VuePrincipale main;
	static BD maBD=BD.getBD();
	ArrayList<Personne> pActives=new ArrayList<Personne>();
	ArrayList<String> pInactives=new ArrayList<String>();
	public static void main(String[] args) {
		try {
			Application monApp=new Application();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Application() throws IOException{
		Reseau.getReseau().addObserver(this);
		/*Enumeration<NetworkInterface> net= NetworkInterface.getNetworkInterfaces();//to work offline
		for(byte m:net.nextElement().getHardwareAddress())
		System.out.print(m);
		NetworkInterface.getByInetAddress(new InetAddress("127.0.0.1")); */
        byte[] m=NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length; i++) {
			sb.append(String.format("%02X%s", m[i], (i < m.length - 1) ? "-" : ""));		
		}
	    String mac=sb.toString();
	    System.out.print(mac);
		user= new Utilisateur(mac.hashCode(),InetAddress.getLocalHost()); //fixe par poste (adresse mac by eg)
		Reseau.getReseau().getActiveUsers(user.getPersonne());
		//conv=new HashMap<String,ConversationGui>;
		main=new VuePrincipale(this);
	}
	String getPseudo() {
		return user.getPseudo();
	}
	void sendActiveUserPseudo() {
		try {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.ALIVE,user.getPersonne()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//after check unicty pseudo
	static void sendPseudoSwitch(String old, String newPseudo,long  id) {
		try {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.SWITCH,user.getPersonne(),newPseudo));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		maBD.delIdPseudoLink(old);
		maBD.setIdPseudoLink(newPseudo,id);
	}
	static boolean checkUnicity(String pseudo) {
		return maBD.checkUnicity();
	}
	void deconnexion(String pseudo) {
		try {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.DECONNECTION,user.getPersonne()));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Personne> getpActives() {
		return pActives;
	}
	/*public ArrayList<String> getPseudoTalked(){
		maBD.getPseudoTalked(user.getId());
	}*/
	@Override
public void update(Observable o, Object arg) {
		//try convert arg to message 
		  if (arg instanceof Message) {  
	           Message message = (Message) arg;  
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   main.update(message.getEmetteur(),message);
		           maBD.addData(message,maBD.getIdPersonne(message.getEmetteur().getPseudo())); //SAVE BD LE MESSAGE RECU
	           }
	           else if(message.getType()==Message.Type.SWITCH) {
	        	   long id =maBD.getIdPersonne(message.getEmetteur().getPseudo());
	        	   maBD.delIdPseudoLink(message.getEmetteur().getPseudo());
	       		   maBD.setIdPseudoLink(message.getNewPseudo(),id);
	           }
	           else if(message.getType()==Message.Type.DECONNECTION) {
	        	   main.deconnection(message.getEmetteur());
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
	}
}