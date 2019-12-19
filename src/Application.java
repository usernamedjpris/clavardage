
import java.awt.EventQueue;
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
		NetworkInterface.getByInetAddress(new InetAddress("127.0.0.1")); */
        byte[] m=NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length; i++) {
			sb.append(String.format("%02X%s", m[i], (i < m.length - 1) ? "-" : ""));		
		}
	    String mac=sb.toString();
	    System.out.print(mac);
		user= new Utilisateur(mac.hashCode(),InetAddress.getLocalHost()); //fixe par poste (adresse mac by eg)
		
		//conv=new HashMap<String,ConversationGui>;
		Personne jeje = new Personne(InetAddress.getLocalHost(), "jejedu31", true); 
		Personne remi = new Personne(null, "remidu31", false );
		model.addElement(new SimpleEntry<>("Jérémie (connecté)", jeje));
		model.addElement(new SimpleEntry<>("Rémi (déconnecté)", remi));
		main=new VuePrincipale(this,model);
		pathDownload=maBD.getDownloadPath();
		
		/* javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });*/
		//test UDP
		Reseau.getReseau().getActiveUsers(user.getPersonne());
		Reseau.getReseau().sendUDP(new Message("bonsoir".getBytes(),jeje,jeje));
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
	String getPseudo() {
		return user.getPseudo();
	}
	Personne getPersonne() {
		return user.getPersonne();
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
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   System.out.print("HERE def ! \n ");
	        	   System.out.print("Pseudo :"+message.getEmetteur().getPseudo()+"\n");
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
	        	   sendActiveUserPseudo();
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
		// TODO Auto-generated method stub
		System.out.print("\n Deconnecté ! \n");
	}
	public File getDownloadPath() {
		return pathDownload;
	}
	public void setDownloadPath(File file) {
	pathDownload=file;
	maBD.setDownloadPath(file);
	}
	public void setPseudoUser(String uname) {
		main.changePseudo(uname);
		sendPseudoSwitch(user.getPseudo(), uname, user.getId());
		user.setPseudo(uname);
	}
}