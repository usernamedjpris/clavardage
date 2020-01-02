
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

//tips: ctrl +r =run (me)
//ctrl+maj+F11=code coverage (standard)
public class Application implements Observer {
	static Personne user;
	VuePrincipale main;
	BD maBD=BD.getBD();
	DefaultListModel<Personne> model = new DefaultListModel<>();
	File pathDownload;

	public static void main(String[] args) {
			new Application();

	}
	String findMac(InetAddress ip) throws SocketException {
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
			 	    //si getLoaclHost n'a pas marché correctement (on veut de l'IPV4) 
			 	    if(ip.isLoopbackAddress() && (ip instanceof Inet4Address)) {
			 	   for(Enumeration<InetAddress> ips = network.getInetAddresses(); ips.hasMoreElements();){
			 		  InetAddress in = (InetAddress) ips.nextElement();
			 		   if(!in.isLoopbackAddress())
			 	    ip=in;
			 	   }
			 	    }
			 	    break;
			    }
		}
		if(mac.equals("")) {
			JOptionPane.showMessageDialog(null, "Hum...Il semblerait que vous n'avez pas de carte réseau, ce chat ne fonctionnera pas sans réseau :p ", "ErrorBox " + "📛", JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		return mac;
	}
	void tests() {

		Personne remi = new Personne(null, "lol1", false,1L );
		Personne jeje = new Personne(null, "lol2", false,2L );
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
		//Conversation c = new Conversation(remi,messages);
		//main.setHtmlView(c);*/
	}
	
	Application(){
		Reseau.getReseau().addObserver(this);
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
		String mac= findMac(ip);
		//System.out.print("ip: "+ip.toString());
		user= new Personne(ip, "moi",true,new Long(mac.hashCode())); //fixe par poste (adresse mac by eg)
	     Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.WHOISALIVE,user));
	     //on obtient les pseudos des gens sur le réseaux avant de demander à l'user d'entrer son pseudo
	     //+actualisation des connexions/deconnexions en continu
	     //<=> aussi sûr que d'envoyer "qui a ce pseudo ?" et un timeout (dans les 2 cas, en cas de choix simultanés (+/- la durée d'envoi d'une trame))=> fail
	     //=> probabilité extremement faible, limite actuelle pour garder un modele simple
	    new VueChoixPseudo(this,false);
	    Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.CONNECTION,user));
	    model.addElement(user);
	    maBD.setIdPseudoLink(user.getPseudo(), user.getId());
		pathDownload=maBD.getDownloadPath();
		main=new VuePrincipale(this,model);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		//tests();
	}
	String getPseudo() {
		return user.getPseudo();
	}
	Personne getPersonne() {
		return user;
	}
	void sendActiveUserPseudo(Personne to) {
			Reseau.getReseau().sendUDP(new Message(Message.Type.ALIVE,user,to));
	}
	boolean checkUnicity(String pseudo) {
		for(Object i : model.toArray()) {
			Personne v=(Personne) i;
			if((v.getConnected() && v.getPseudo().equals(pseudo)))
				return false;
		}
			return true;
		}
	void deconnexion(String pseudo) {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.DECONNECTION,user));
	}

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
	         //do not reply to yourself ^^
        	   if(message.getEmetteur().getId()!= user.getId()) {
	           System.out.print("\n Reception de :"+message.getType().toString()+" de la part de "+message.getEmetteur().getPseudo()+"("+message.getEmetteur().getAdresse().toString()+"\n" );
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   main.update(message.getEmetteur(),message);
		           maBD.addData(message); //SAVE BD LE MESSAGE RECU
	           }
	           else if(message.getType()==Message.Type.SWITCH) {
	        	   long id=maBD.getIdPersonne(message.getEmetteur().getPseudo());
	        	  /* maBD.delIdPseudoLink(message.getEmetteur().getPseudo());
	       		   maBD.setIdPseudoLink(message.getNewPseudo(),id);*/
	       		 int index = model.indexOf(message.getEmetteur());
	       		 model.get(index).setPseudo(message.getNewPseudo());
	       		 main.updateList();
	           }
	           else if(message.getType()==Message.Type.DECONNECTION) {
	        	   int index = model.indexOf(message.getEmetteur());
	        	   if(index >= 0) {
	        		   model.get(index).setConnected(false);
	        	   }
	           }
	           else if(message.getType()==Message.Type.ALIVE || message.getType()==Message.Type.CONNECTION) {
	        	  	  //add sender to active user
		        	  int index = model.indexOf(message.getEmetteur());
		        	  if(index <0) {
		        	   model.add(0, message.getEmetteur());
		        	  }
		        	  else {
		        		  Personne p=model.get(index);
		        		  p.setConnected(true);
		        		  p.setInetAdress(message.getEmetteur().getAdresse());
		        	  }
		        	  maBD.setIdPseudoLink(message.getEmetteur().getPseudo(), message.getEmetteur().getId());
	           }
	           else if(message.getType()==Message.Type.WHOISALIVE) {  
	        	   sendActiveUserPseudo(message.getEmetteur());
	           }

	           else
	        	   System.out.print("WARNING unknow message type !");
        	   }

	        }
	}
	public void sendDisconnected() {
			Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.DECONNECTION,user));
	}
	public File getDownloadPath() {
		return pathDownload;
	}
	public void setDownloadPath(File file) {
	pathDownload=file;
	maBD.setDownloadPath(file);
	}
	public void setPseudoUserSwitch(String uname) {
		maBD.delIdPseudoLink(user.getPseudo());
		maBD.setIdPseudoLink(uname,user.getId());
		int index =model.indexOf(user);
		model.get(index).setPseudo(uname);
		main.changePseudo(uname);
		user.setPseudo(uname);
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.SWITCH,user,uname));
	
	}
	public void setPseudoUser(String uname) {
		user.setPseudo(uname);
	}

}
