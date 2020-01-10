
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

//tips: ctrl +r =run (me) ctrl+F11 (standard)
//ctrl+maj+F11=code coverage (standard)
@SuppressWarnings("deprecation")
public class ControleurApplication implements Observer {
	private Personne user;
	private final int portTcp=1030; //config.ini
	private VuePrincipale main;
	private BD maBD=BD.getBD();
	private DefaultListModel<Personne> model = new DefaultListModel<>();
	private File pathDownload;
	private InetAddress localIp;
	private Boolean initialized=false;
	private String pseudoWaiting="";
	private boolean answerPseudo;
	public static void main(String[] args) {
			new ControleurApplication();

	}
	String findMac() throws SocketException {
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
			 	    //find good local ip
					try(DatagramSocket s=new DatagramSocket())
					{
					    try {
							s.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 0);
							localIp=s.getLocalAddress();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
					}
			 	    //si getLoaclHost n'a pas marché correctement (on veut de l'IPV4) 
			 	    if(localIp.isLoopbackAddress() || !(localIp instanceof Inet4Address)) {
			 	   for(Enumeration<InetAddress> s = network.getInetAddresses(); s.hasMoreElements();){
			 		  InetAddress in = (InetAddress) s.nextElement();
			 		 // System.out.print(" \nlocalIP s found : " +in.toString() + " ? "+ (!in.isLoopbackAddress() && in instanceof Inet4Address));
			 		 System.out.print(" \nloop: " +in.toString() + " ? "+ (in.isLoopbackAddress()));
			 		  if(!in.isLoopbackAddress() && in instanceof Inet4Address)
			 			  localIp=in;
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
	ControleurApplication(){
		Reseau.getReseau().init(portTcp);
		Reseau.getReseau().addObserver(this);
		try {
			localIp = InetAddress.getLocalHost();
		//System.out.print(localIp.toString() + " is local ? : "+localIp.isLoopbackAddress());
		String mac= findMac();
		System.out.print("ip: "+localIp.toString()+" id: "+mac.hashCode());
		user= new Personne(localIp, portTcp,"moi",true,(long)mac.hashCode()); //fixe par poste (adresse mac by eg)
	     Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.WHOISALIVE,user));
	     //on obtient les pseudos des gens sur le réseaux avant de demander à l'user d'entrer son pseudo
	     //+actualisation des connexions/deconnexions en continu
	     //<=> aussi sûr que d'envoyer "qui a ce pseudo ?" et un timeout (dans les 2 cas, en cas de choix simultanés (+/- la durée d'envoi d'une trame))=> fail
	     //=> probabilité extremement faible, limite actuelle pour garder un modele simple
	    new VueChoixPseudo(this,false);
	    initialized=true;
	    Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.CONNECTION,user));
	    model.addElement(user);
	   // model.addElement(new Personne(localIp, "moi",true,(long)mac.hashCode()));
	   // maBD.setIdPseudoLink(user.getPseudo(), user.getId());
		pathDownload=maBD.getDownloadPath();
		main=new VuePrincipale(this,model);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		//tests();
		//testsBD();
	}
	String getPseudo() {
		return user.getPseudo();
	}
	Personne getPersonne() {
		return user;
	}
	void sendActiveUserPseudo(Personne to) {
			Reseau.getReseau().sendUDP(new Message(Message.Type.ALIVE,null,user,to));
	}
	boolean checkUnicity(String pseudo) {
		answerPseudo=true;
		pseudoWaiting=pseudo;
		user.setPseudo(pseudo);
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.ASKPSEUDO,user));
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(answerPseudo) {
		for(Object i : model.toArray()) {
			Personne v=(Personne) i;
			if((v.getId()!=user.getId() && v.getConnected() && v.getPseudo().equals(pseudo)))
				return false;
		}
			return true;
		}
		else
			return false;
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
		//on ne repond pas tant que l'on n'est pas initialis� (avec un pseudo)
		  if (arg instanceof Message) {
	           Message message = (Message) arg;
	         //do not reply to yourself broadcast ^^ //DEFAULT => possibilité de se parler à soi-même ONLY FOR TEST (simple send en prod)
        	   if(message.getEmetteur().getId()!= user.getId() || message.getType()==Message.Type.DEFAULT || message.getType()==Message.Type.FILE) {
	           System.out.print("\n Reception de :"+message.getType().toString()+" de la part de "+message.getEmetteur().getPseudo()+"("+message.getEmetteur().getAdresse().toString()+"\n" );
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   main.update(message.getEmetteur(),message,false);
		           maBD.addData(message); //SAVE BD LE MESSAGE RECU
		          // maBD.printMessage();
	           }
	           else if(message.getType()==Message.Type.FILE) {
	        	   System.out.print(" \n FILE reçu !");
	        	   main.update(message.getEmetteur(),message,false);
	        	   try {
	        		   String basePath=maBD.getDownloadPath().getCanonicalPath()+"/";
	        		   File newFile=new File(basePath+message.getNameFile());
	        		   int index=0;
	        		   if(newFile.exists())
	        		   while ((newFile = new File(basePath+"("+index+")"+message.getNameFile())).exists()) {
	        			    index++;
	        			}
	        		   Path p=newFile.toPath();
					Files.write(p, message.getData());
				} catch (IOException e) {
					e.printStackTrace();
				}
		           maBD.addFile(message,message.getNameFile()); //SAVE BD LE MESSAGE RECU
	           }
	           else if(message.getType()==Message.Type.SWITCH) {
	        	  // long id=maBD.getIdPersonne(message.getEmetteur().getPseudo());
	        	  /* maBD.delIdPseudoLink(message.getEmetteur().getPseudo());*/
	       		   maBD.setIdPseudoLink(message.getEmetteur().getPseudo(),message.getEmetteur().getId());
	        	   for(Object ob: model.toArray()) {
	        		   Personne p =(Personne)ob;
	        		   if(p.getId()==message.getEmetteur().getId()) {
	        			   p.setPseudo(message.getEmetteur().getPseudo());
	        			   break;
	        		   }
	        	   }
	       		 main.updateList();
	           }
	           else if(message.getType()==Message.Type.DECONNECTION) {
	        	  // int index = model.indexOf(message.getEmetteur());
	        	   for(Object ob: model.toArray()) {
	        		   Personne p =(Personne)ob;
	        			   if(p.getId()==message.getEmetteur().getId()) {
	        			   p.setConnected(false);
	        			   break;
	        		   }
	        	   }
	        	   main.updateList();
	           }
	           else if(message.getType()==Message.Type.ALIVE || message.getType()==Message.Type.CONNECTION) {
	        	  	  //add sender to active user
	        	   boolean found=false;
	        	   Personne pers = null;
	        	   for(Object ob: model.toArray()) {
	        		   Personne p =(Personne)ob;
	        			   if(p.getId()==message.getEmetteur().getId()) {
	        			   found=true;
	        			   pers=p;
	        			   break;
	        		   }
	        	   }
		        	  if(!found) {
		        	   model.add(0, message.getEmetteur());
		        	  }
		        	  else {
		        		  pers.setConnected(true);
		        		  pers.setInetAdress(message.getEmetteur().getAdresse());
		        	  }
		        	  maBD.setIdPseudoLink(message.getEmetteur().getPseudo(), message.getEmetteur().getId());
	           }
	           else if(message.getType()==Message.Type.WHOISALIVE ) { 
	        	   if(initialized)
	        	   sendActiveUserPseudo(message.getEmetteur());
	           }
	           else if(message.getType()==Message.Type.ASKPSEUDO) {
	        	   if(pseudoWaiting.equals(message.getEmetteur().getPseudo()));
	        	   Reseau.getReseau().sendUDP(new Message(Message.Type.REPLYPSEUDO,null,user,message.getEmetteur()));
	           }
	           else if(message.getType()==Message.Type.REPLYPSEUDO)
	        	   answerPseudo=false;
	           else
	        	   System.out.print("WARNING unknow message type : " + message.getType().toString());
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
		/*maBD.delIdPseudoLink(user.getPseudo());*/
		maBD.setIdPseudoLink(uname,user.getId());
		main.changePseudo(uname);
		user.setPseudo(uname);
		Reseau.getReseau().sendDataBroadcast(new Message(Message.Type.SWITCH,user));
	
	}
	public void setPseudoUser(String uname) {
		user.setPseudo(uname);
	}
	/** 
	* @param tosend texte à envoyer à activeUser
	 */
	public void sendMessage(String tosend, Personne to) {
		Message m =new Message(Message.Type.DEFAULT,tosend.getBytes(), user, to);
		Reseau.getReseau().sendTCP(m);
		main.update(to,m,true);
		maBD.addData(m);
	}
	/**
	 * 
	 * @param file fichier à envoyer
	 * @param name nom du fichier
	 */
	public void sendMessage(byte[] file, String name, Personne to) {
		
		Message m =new Message(file, user, to,name);
		Reseau.getReseau().sendTCP(m);
		main.update(to,m,true);
		maBD.addData(m);
	}
	public ArrayList<Message> getHistorique(Personne to) {
			return maBD.getHistorique(user,to);
	}

}
