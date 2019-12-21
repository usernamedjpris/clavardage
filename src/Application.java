
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.AbstractMap.*;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//tips: ctrl +r =run (me)
//ctrl+maj+F11=code coverage (standard)
public class Application implements Observer {
	static Utilisateur user;
	VuePrincipale main;
	BD maBD=BD.getBD();
	DefaultListModel<Map.Entry<String, Personne>> model = new DefaultListModel<>();
	ArrayList<Personne> pActives=new ArrayList<Personne>();
	ArrayList<String> pInactives=new ArrayList<String>();
	File pathDownload;

	public static void main(String[] args) {
		try {
			new Application();
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
		NetworkInterface in=NetworkInterface.getByInetAddress(InetAddress.getByName(("127.0.0.1")));
		byte[] m=in.getHardwareAddress();*/
		String mac="";
		for(Enumeration<NetworkInterface> enm = NetworkInterface.getNetworkInterfaces(); enm.hasMoreElements();){
			  NetworkInterface network = (NetworkInterface) enm.nextElement();
			  byte[] m=network.getHardwareAddress();
			    if((null != m) && (m.length>0)){
			    	 StringBuilder sb = new StringBuilder();
			 		for (int i = 0; i < m.length; i++) {
			 			sb.append(String.format("%02X%s", m[i], (i < m.length - 1) ? "-" : ""));
			 		}
			 	    mac=sb.toString();
			 	    break;
			    }
		}
		if(mac.equals("")) {
			JOptionPane.showMessageDialog(null, "Hum...Il semblerait que vous n'avez pas de carte réseau, ce chat ne fonctionnera pas sans réseau :p ", "ErrorBox " + "📛", JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		else {
		System.out.print(mac);
		user= new Utilisateur(mac.hashCode(),InetAddress.getLocalHost()); //fixe par poste (adresse mac by eg)
	    new VueChoixPseudo(this,false);
		//conv=new HashMap<String,ConversationGui>;

		Personne byDefault = new Personne(InetAddress.getLocalHost(), mac,true);
		
	    model.addElement(new SimpleEntry<>("(vous-m�me)", byDefault));
		main=new VuePrincipale(this,model);
		pathDownload=maBD.getDownloadPath();


		//test UDP
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.CONNECTION,user.getPersonne()));
		//getActiveUsers
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.WHOISALIVE,user.getPersonne()));
		//Reseau.getReseau().sendUDP(new Message("bonsoir".getBytes(),jeje,jeje));

		Personne remi = new Personne(null, mac, false );
		Personne jeje = new Personne(null, mac, false );
		//test VuePrincipale
		ArrayList<Message>messages = new ArrayList<Message>();
		messages.add(new Message("hey !".getBytes(), remi, jeje));
		messages.add(new Message("hey !".getBytes(), jeje, remi));
		messages.add(new Message("ça marche ton affichage de la conversation ?".getBytes(), remi, jeje));
		messages.add(new Message("yep !".getBytes(), jeje, remi));
		messages.add(new Message("😎".getBytes(), remi, jeje));
		messages.add(new Message("je cherche une idée pour une conversation fictive histoire de tester les fonctionnalités de notre SuperClavardeur™ par exemple (pour voir par exemple si une phrase très très longue sera bien traitée à l'affichage). Tu en aurais une ?".getBytes(), jeje, remi));
		messages.add(new Message("non".getBytes(), remi, jeje));
		messages.add(new Message("🥇".getBytes(), jeje, remi));
		messages.add(new Message("🎯".getBytes(), jeje, remi));
		messages.add(new Message("il fait 5℃".getBytes(), jeje, remi));
		messages.add(new Message("tu fais quoi ?!?".getBytes(), remi, jeje));
		messages.add(new Message("je teste les caractères spéciaux pour plus de fun !".getBytes(), jeje, remi));
		messages.add(new Message("(☞ﾟヮﾟ)☞".getBytes(), remi, jeje));
		messages.add(new Message("☜(ﾟヮﾟ☜)".getBytes(), jeje, remi));
		Conversation c = new Conversation(remi,messages);
		main.setHtmlView(c);
		}
	}
	String getPseudo() {
		return user.getPseudo();
	}
	Personne getPersonne() {
		return user.getPersonne();
	}
	void sendActiveUserPseudo(Personne to) {
		try {
			Reseau.getReseau().sendUDP(new Message(Message.Type.ALIVE,user.getPersonne(),to));
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
		BD.getBD().delIdPseudoLink(old);
		BD.getBD().setIdPseudoLink(newPseudo,id);
	}
	static boolean checkUnicity(String pseudo) {
		return BD.getBD().checkUnicity();
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
		/* si  IMAGE to write file :
		 * byte[] encoded = key.getEncoded();
FileOutputStream output = new FileOutputStream(new File("target-file"));
IOUtils.write(encoded, output);
		 */
		  if (arg instanceof Message) {
	           Message message = (Message) arg;
	           System.out.print("\n R�ception de :"+message.getType().toString()+" de la part de "+message.getEmetteur().getPseudo()+"\n" );
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
	        	   pActives.remove(message.getEmetteur());
	           }
	           else if(message.getType()==Message.Type.ALIVE) {
	        	   pActives.add(message.getEmetteur());
	        	  // pseudoToPerson.put(message.getEmetteur().getPseudo(), message.getEmetteur());
	           }
	           else if(message.getType()==Message.Type.WHOISALIVE) {
	        	   sendActiveUserPseudo(message.getEmetteur());
	           }
	           else if(message.getType()==Message.Type.CONNECTION) {
	        	   pActives.add(message.getEmetteur());

	        	  // pseudoToPerson.put(message.getEmetteur().getPseudo(), message.getEmetteur());
	        	  int index = model.indexOf(message.getEmetteur());
	        	  if(index <0) {
	        	   model.add(0, new SimpleEntry<String, Personne>(message.getEmetteur().getPseudo(),message.getEmetteur()));
	        	  }
	        	  else {
	        		  Personne p=model.get(index).getValue();
	        		  p.setConnected(true);
	        		  p.setInetAdress(message.getEmetteur().getAdresse());
	        	  }

	           }
	           else
	        	   System.out.print("WARNING unknow message type !");

	        }
	}
	public void sendDisconnected() {
		try {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.DECONNECTION,user.getPersonne()));
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public File getDownloadPath() {
		return pathDownload;
	}
	public void setDownloadPath(File file) {
	pathDownload=file;
	maBD.setDownloadPath(file);
	}
	public void setPseudoUserSwitch(String uname) {
		main.changePseudo(uname);
		sendPseudoSwitch(user.getPseudo(), uname, user.getId());
		user.setPseudo(uname);
	}
	public void setPseudoUser(String uname) {
		user.setPseudo(uname);
	}

}
