
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.NatInit;
import org.bitlet.weupnp.PortMappingEntry;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.xml.sax.SAXException;

import com.clava.serializable.Group;
import com.clava.serializable.Interlocuteurs;
import com.clava.serializable.Message;
import com.clava.serializable.Personne;


//tips: ctrl +r =run (me) ctrl+F11 (standard)
//ctrl+maj+F11=code coverage (standard)
//ObjectAid UML (retro URL)
//public class ControleurApplication implements Observer {
public class ControleurApplication implements PropertyChangeListener{
	private Personne user;
	private VuePrincipale main;
	private BD maBD=BD.getBD();
	private DefaultListModel<Interlocuteurs> model = new DefaultListModel<Interlocuteurs>();
	ArrayList<Integer> localConnexion=new ArrayList<>();
	private File pathDownload;
	private Boolean initialized=false;
	private String pseudoWaiting="";
	private boolean answerPseudo;
	private Object mutex = new Object();
	Wini ini;
	private InetAddress localIp;
	/**
	 * main thread
	 * @param args
	 */
	public static void main(String[] args) {
			new ControleurApplication();

	}
	/**
	 * Interroge le serveur toute les 5s pour mettre à jour les destinataires présents "ALIVE".
	 */
	void configServeur() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  Reseau.getReseau().sendHttp(Message.Factory.whoIsAliveBroadcast(user));
			  }} , 5000, 5000);
		}
	/**
	 * Fonction de découverte automatique de l'ip (IPV4) locale de l'utilisateur 
	 * @return ip locale
	 * NB : si l'application echoue à trouver l'ip locale on peut toujour la rentrer manuellement dans config.ini
	 * @see ControleurApplication#init() 
	 */
	InetAddress findIp() {
		InetAddress localIp;
		try {
			DatagramSocket so=new DatagramSocket();
			so.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 0);
			localIp=so.getLocalAddress();
			so.close();
			//System.out.print(" adresse  ip : "+ localIp.toString());
	 	    if(localIp.isLoopbackAddress() || !(localIp instanceof Inet4Address)) {
	 	    for(Enumeration<NetworkInterface> enm = NetworkInterface.getNetworkInterfaces(); enm.hasMoreElements();){
	 	    	NetworkInterface network = (NetworkInterface) enm.nextElement();
		 	    //si getLoaclHost n'a pas marché correctement (on veut de l'IPV4) 
		 	    for(Enumeration<InetAddress> s = network.getInetAddresses(); s.hasMoreElements();){
			 		InetAddress in = (InetAddress) s.nextElement();
			 		// System.out.print(" \nlocalIP s found : " +in.toString() + " ? "+ (!in.isLoopbackAddress() && in instanceof Inet4Address));
			 		// System.out.print(" \nloop: " +in.toString() + " ? "+ (in.isLoopbackAddress()));
			 		if(!in.isLoopbackAddress() && in instanceof Inet4Address)
			 			localIp=in;
				 	}
				}
				//find good local ip last chance 
				if(localIp.isLoopbackAddress() || !(localIp instanceof Inet4Address)) {
					localIp = InetAddress.getLocalHost(); //buggy one
				}
	 	    }
		return localIp;
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "Hum...Il semblerait que nous ne soyons pas capables d'obtenir votre adresse ip,"
					+"vous pouvez essayer de la fournir manuellement dans config.ini partie avancée :  \n" + 
					"doNotUseAutoIpAndUseThisOne", "ErrorBox " + "📛", JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		} catch (SocketException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Hum...Il semblerait que nous ne soyons pas capables d'obtenir votre adresse ip,"
					+"vous pouvez essayer de la fournir manuellement dans config.ini partie avancée :  \n" + 
					"doNotUseAutoIpAndUseThisOne", "ErrorBox " + "📛", JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		//on n'y arrive jamais
		return (InetAddress) new Object();
				
	}
	/**
	 * Fonction de découverte automatique de l'@mac locale de l'utilisateur 
	 * @return mac locale
	 * NB : si l'application echoue à trouver l'@mac locale on peut toujour la rentrer manuellement dans config.ini
	 * @see ControleurApplication#init() 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	String findMac() throws SocketException, UnknownHostException {
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
			JOptionPane.showMessageDialog(null, "Hum...Il semblerait que nous ne soyons pas capables d'obtenir votre adresse mac,"
					+"vous pouvez essayer de la fournir manuellement dans config.ini partie avancée :  \n" + 
					"doNotUseAutoMacAndUseThisOne", "ErrorBox " + "📛", JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		return mac;
	}
	/**
	 * initialisation du reseau (ip, mac et port locaux) + [Design Pattern Observers]
	 */
	void init() {
		String ipServer=null;
		InetAddress ipForceLocal=null;
		String mac =null;
		boolean forceUseIp=false;
		boolean forceUseMac=false;
		try {
			ini = new Wini(new File("config.ini"));
		} catch (InvalidFileFormatException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		int portTcp =ini.get("IP", "TCPport", int.class);
		int portUDP =ini.get("IP", "UDPport", int.class);
		int portServer =ini.get("IP", "publicServerPort",int.class);
		pathDownload=new File(ini.get("DOWNLOAD", "path",String.class));
		ipServer =ini.get("IP", "publicServerIp", String.class);//InetAddress.getByName(
				try {
					String s=ini.get("ADVANCED", "doNotUseAutoIpAndUseThisOne", String.class);
					if(!s.equals("")) {
						forceUseIp=true;
						int a;
						ipForceLocal =InetAddress.getByName(s);
					}
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, " L'adresse IP fournie avec le flag doNotUseAutoIpAndUseThisOne dans config.ini n'est pas au format correct,"+
				" vérifiez votre saisie ou supprimez ce champs", "Web Server", JOptionPane.ERROR_MESSAGE);	
					System.exit(0);
				}
				String mac_manuel=ini.get("ADVANCED", "doNotUseAutoMacAndUseThisOne", String.class);
				if(!mac_manuel.equals("")) {
					forceUseMac=true;
					mac=mac_manuel;
				}
		//System.out.print("data :" +portTcp+" "+portUDP+" "+portServer+" "+ini.get("IP", "publicServerIp", String.class)+" "+ini.get("IP", "doNotUseAutoIpAndUseThisOne", String.class));
		new NatInit(portTcp);
		Reseau.getReseau().init(portTcp,portUDP,ipServer,portServer);
		Reseau.getReseau().addPropertyChangeListener(this);//.addObserver(this);
		try {
		if(forceUseIp)
			localIp=ipForceLocal;
		else
			localIp=findIp();
		if(!forceUseMac)
			mac=findMac();
		
		System.out.print("ip: "+localIp.toString()+" id: "+mac.hashCode());
		user= new Personne(new SimpleEntry<InetAddress, Integer>(localIp, portTcp),"moi",true,mac.hashCode()); //fixe par poste (adresse mac by eg)
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Constructeur ControleurApplication 
	 * <p>initialise le réseau, la Vue Pseudo (bloquante) et affiche la Vue Principale en consultant la BD + [Design Pattern Observers]</p>
	 */
	ControleurApplication(){
		init();
	    //on récupère les gens avec qui on a déjà parlé #offline reading
	    for(Interlocuteurs p: maBD.getInterlocuteursTalked(user)) {
			if(p.getId()!=user.getId())
			model.addElement(p);
	    }
	    Reseau.getReseau().sendDataBroadcast(Message.Factory.whoIsAliveBroadcast(user));
	    new VueChoixPseudo(this,false);
	    main=new VuePrincipale(this,model);
	    initialized=true;
	    Reseau.getReseau().sendDataBroadcast(Message.Factory.userConnectedBroadcast(user));
	    //on s'ajoute après que le serveur sait que l'on est connecte
	    //rq: on s'ajoute à la main car on bloque les broadcast venant de nous même en réception (pollution)
	    model.addElement(user);
	    main.firstSelection();
	    main.updateList();
		configServeur();
	}
	/**
	 * Retourne le pseudo utilisateur
	 * @return pseudo utilisateur
	 */
	String getPseudo() {
		return user.getPseudo();
	}
	/**
	 * Retourne la personne utilisateur
	 * @return personne utilisateur
	 */
	Interlocuteurs getPersonne() {
		return user;
	}
	/**
	 * Envoie un message ALIVE (emetteur = utilisateur) en broadcast UDP sur le réseau local
	 * @param to
	 */
	void sendActiveUserPseudo(Interlocuteurs to) {
			Reseau.getReseau().sendUDP(Message.Factory.userIsAlive(user, to));
	}
	/**
	 * checkUnicity envoie un message (emetteur = user, user.pseudo = newpseudo) ASKPSEUDO à tout le monde
	 * s'il ne reçoit pas de réponse (message REPLYPSEUDO) dans les 3 secondes alors estime que pseudo pas deje pris
	 * @param pseudo
	 * @return false si deja pris true sinon
	 */
	boolean checkUnicity(String pseudo) {
		synchronized (mutex) {
			answerPseudo=true;
			pseudoWaiting=pseudo;
			user.setPseudo(pseudo);
		}
		Reseau.getReseau().sendDataBroadcast(Message.Factory.askPseudoOkBroadcast(user));
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(answerPseudo) {
			for(Object i : model.toArray()) {
				Interlocuteurs v=(Interlocuteurs) i;
				if((v.getId()!=user.getId() && v.getConnected() && v.getPseudo().equals(pseudo)))
					return false;
			}
			return true;
		}
		else {
			synchronized (mutex) {
			pseudoWaiting="";//on arrête de l'attendre (déjà attribué ou va l'être)
			}
			return false;
		}
	}
	/**
	 * PropertyChange reçoit les messages de Reseau [Design Pattern Observers]
	 * et traite selon le type de message 
	 */
	@Override
	synchronized public void propertyChange(PropertyChangeEvent evt) {
		//try convert arg to message
		/* si  IMAGE to write file :
		 * byte[] encoded = key.getEncoded();
		FileOutputStream output = new FileOutputStream(new File("target-file"));
		IOUtils.write(encoded, output);
		 */
		//on ne repond pas tant que l'on n'est pas initialis� (avec un pseudo)
		//System.out.print(" \n type d'évenements: : "+evt.getPropertyName());
		if(evt.getPropertyName().equals("serveur") && evt.getNewValue() instanceof Message) {
			Message message = (Message) evt.getNewValue();
			
			//si une personne nous envoie les infos localement pas besoin d'écouter à retard les mêmes infos venant du serveur
			//if(!localConnexion.contains(message.getEmetteur().getId())) {
			/*System.out.print(" \n Serveur send us :" +message.getType()+" avec ");
			for(Interlocuteurs i:message.getEmetteur().getInterlocuteurs()) {
				System.out.print( "\n pseudo :"+i.getPseudo()+"  "+i.getAddressAndPorts());
			}*/
			if(message.getType()==Message.Type.OKSERVEUR) {
				
			}
			else if(message.getType()==Message.Type.REPLYPSEUDO)
	        	   answerPseudo=false;
			else if(message.getType()==Message.Type.ALIVE){
			
				for(Object ob: model.toArray()) {
	        		   Interlocuteurs p =(Interlocuteurs)ob;
	        		   boolean found=false;
	        		   for(Interlocuteurs i:message.getEmetteur().getInterlocuteurs()) {
			        		   if(p.getId()==i.getId() && !localConnexion.contains(p.getId())) {
			        			   try {
			        				   //le serveur contient tjrs le pseudo le + à jour
			        				   if(!p.getPseudo().equals(i.getPseudo())) {
					        				  System.out.print(" \n [serveur] MAJ du pseudo de : "+p.getPseudo());
											  p.setPseudo(i.getPseudo());
			        				   }//si app crashe, on ignore l'nfo du serveur disant qu'on était déjà connecté (notre connexion va régulariser
			        				   //la situation
			        				   if(!p.getConnected() && i.getId()!=user.getId()) {
			        					   System.out.print(" \n [serveur] Connexion de : "+p.getPseudo()+" à "+i.getAddressAndPorts().get(0));
			        					   p.setConnected(true);
			        					/// ok if NAT well config #upnp or manually 
			        					   p.setAddressAndPorts(i.getAddressAndPorts().get(0));
			        				   }
									found=true;
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								}
			        		   }
	        		   }	
	        		 //si la personne n'est pas présente dans la liste retournée par le serveur et n'est pas un groupe
	        		   //(absent du serveur), c'est qu'elle s'est déconnectée
	        		   
	        		  if(!found && p.getInterlocuteurs().size()<2 && p.getConnected() && !localConnexion.contains(p.getId()))
						try {
							System.out.print(" \n Deconnexion de: "+p.getPseudo());
							p.setConnected(false);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
				}
				//si la liste du serveur contient un nouveau venu on l'ajoute
				 for(Interlocuteurs i:message.getEmetteur().getInterlocuteurs()) {
	        		   boolean found=false;
					 for(Object ob: model.toArray()) {
		        		   Interlocuteurs p =(Interlocuteurs)ob;
		        		   if(i.getId()==p.getId() )
		        			   found=true;
					 }
					 //on ne se connecte pas soi même #régularisation 
					 if(!found && i.getId()!= user.getId() && !localConnexion.contains(i.getId())) {
						 System.out.print(" \n Connexion de: "+i.getPseudo());
						 model.add(0, i);
						 maBD.setIdPseudoLink(i.getPseudo(),i.getId());
					 }
				 }
			}else
				System.out.print(" Warning unknow message type !");
		}else {
		  if (evt.getNewValue() instanceof Message) {
	           Message message = (Message) evt.getNewValue();
	           //pas de réponse à notre propre broadcast ^^  
	           //pas d'affichage des messages qu'on envoie dans un groupe où on est présent (aussi envoyé à soi #même id everywhere))
	           //possibilité de se parler à soi même
        	   if((message.getDestinataire() == null && message.getEmetteur().getId()!=user.getId()) 
        			   || !message.getEmetteur().getInterlocuteurs().contains(user)
        			   || message.getDestinataire() != null 
        			   && (message.getDestinataire().getId()== user.getId() 
        			   && message.getEmetteur().getId()==user.getId()))
        			    {
	           System.out.print("\n Reception de :"+message.getType().toString()+" de la part de "+message.getEmetteur().getPseudo()+
	        		   "("+message.getEmetteur().getAddressAndPorts().toString()+")"+"\n" );
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   if(initialized) {
	        		   if(message.getDestinataire().getInterlocuteurs().size()>1)
		        		   main.update(message.getDestinataire(),message,false); 
		        	   else
		        		   main.update(message.getEmetteur(),message,false);
		           maBD.addData(message); //SAVE BD LE MESSAGE RECU
		          // maBD.printMessage();
	        	   }
	           }
	           else if(message.getType()==Message.Type.FILE) {
	        	   try {
	        		   String basePath=pathDownload.getCanonicalPath()+"/";
	        		   File newFile=new File(basePath+message.getNameFile());
	        		   int index=0;
	        		   if(newFile.exists())
	        		   while ((newFile = new File(basePath+"("+index+")"+message.getNameFile())).exists()) {
	        			    index++;
	        			}
	        		   Path p=newFile.toPath();
					Files.write(p, message.getData());
		        	 message.setNameFile(newFile.getAbsolutePath());
		        	 System.out.print(message.getNameFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	   //si groupe, graphiquement l'emetteur apparait comme étant le groupe
	        	   if(message.getDestinataire().getInterlocuteurs().size()>1)
	        		   main.update(message.getDestinataire(),message,false); 
	        	   else
	        		   main.update(message.getEmetteur(),message,false);
		           maBD.addFile(message); //SAVE BD LE MESSAGE RECU
	           }
	           else if(message.getType()==Message.Type.SWITCH) {
	        	  // long id=maBD.getIdPersonne(message.getEmetteur().getPseudo());
	        	  /* maBD.delIdPseudoLink(message.getEmetteur().getPseudo());*/
	       		   maBD.setIdPseudoLink(message.getEmetteur().getPseudo(),message.getEmetteur().getId());
	        	   for(Object ob: model.toArray()) {
	        		   Interlocuteurs p =(Interlocuteurs)ob;
	        		   if(p.getId()==message.getEmetteur().getId()) {
	        			   try {
							p.setPseudo(message.getEmetteur().getPseudo());
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
	        			   break;
	        		   }
	        	   }
	        	   if(initialized)
	       		 main.updateList();
	           }
	           else if(message.getType()==Message.Type.DECONNECTION) {
	        	  // int index = model.indexOf(message.getEmetteur()); // not working
	        	   //fix via equals redefinition => refactoring possible ! 
	        	   localConnexion.remove(message.getEmetteur().getId());
	        	   for(Object ob: model.toArray()) {
	        		   Interlocuteurs p =(Interlocuteurs)ob;
	        			   if(p.getId()==message.getEmetteur().getId()) {
	        			   try {
							p.setConnected(false);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
	        			   break;
	        		   }
	        	   }
	        	   main.updateList();
	           }
	           else if(message.getType()==Message.Type.ALIVE || message.getType()==Message.Type.CONNECTION) {
	        	  	  //ONLY PERSON SEND IT (le groupe n'a pas de vie propre, quand 
	        	   //tous ses membres sont connectés il devient connecté (absence de broadcast donc )) 
	        	   localConnexion.add(message.getEmetteur().getId());
	        	   boolean found=false;
	        	   for(Object ob: model.toArray()) {
	        		   Interlocuteurs p =(Interlocuteurs)ob;
	        			   if(p.getId()==message.getEmetteur().getId()) {
			        			   found=true;
			        			   try {
											p.setConnected(true);
						        			p.setPseudo(message.getEmetteur().getPseudo());
											p.setAddressAndPorts(new SimpleEntry<>(message.getEmetteur().getAddressAndPorts().get(0)));
											} catch (NoSuchMethodException e) {
												System.out.print(" Erreur ! Un groupe s'est connecté ^^");
												e.printStackTrace();
									}
			        			   break;
	        		   }
	        	   }
		        	  if(!found) {
		        	   model.add(0, message.getEmetteur());
		        	  } 
		        	  
		        	  maBD.setIdPseudoLink(message.getEmetteur().getPseudo(), message.getEmetteur().getId());
		        	  if(initialized)
		        	  main.updateList();
	           }///TODO finish group
	           else if(message.getType()==Message.Type.GROUPCREATION ) { 
	        	   
	        	   if(!model.contains(message.getDestinataire())) {
	        	   //recréation du groupe en local #avec les pointeurs sur lespersonnes que l'on a crées en local #
	        	   //#auto connexion/deconnexion, pseudo switch
	        	   ArrayList<Interlocuteurs> array=new ArrayList<>();
	        	   for(Interlocuteurs i:message.getDestinataire().getInterlocuteurs()) {
	        		   int index =model.indexOf(i);
	        		   if(index != -1)
	        		   array.add(model.get(index));
	        		   else {
	        		   System.out.print("\n Warning !, un de vos amis a créé un groupe avec une personne que vous ne connaissez pas, "
	        		   		+ "ceci peut être dû à un délai réseau, nous ajoutons cette personne" );
	        		   model.add(0,i);
	        		   array.add(i);
	        		   }
	        	   }
	        	   Group g=new Group(array);
	        	   //g.addInterlocuteur(message.getEmetteur());
	        	  // g.removeInterlocuteur(user);
	        	   maBD.addGroup(g.getId(),g.getInterlocuteurs());
	        	   model.add(1, g);
	        	   if(initialized)
	        		   main.updateList();
	        	   }
	           }
	           else if(message.getType()==Message.Type.WHOISALIVE ) { 
	        	   if(initialized)
	        	   sendActiveUserPseudo(message.getEmetteur());
	           }
	           else if(message.getType()==Message.Type.ASKPSEUDO) {
	        	   synchronized (mutex) {
	        	   if(pseudoWaiting.equals(message.getEmetteur().getPseudo()))
	        	   Reseau.getReseau().sendUDP(Message.Factory.usernameAlreaydTaken(user, message.getEmetteur()));
	        	   }
	           }
	           else if(message.getType()==Message.Type.REPLYPSEUDO)
	        	   answerPseudo=false;
	           else
	        	   System.out.print("WARNING unknow message type : " + message.getType().toString());
        	   }

	        }
		}
	}
	/**
	 * Envoie message de déconnexion à tout le monde
	 */
	public void sendDisconnected() {
			Reseau.getReseau().sendDataBroadcast(Message.Factory.userDisconnectedBroadcast(user));
	}
	/**
	 * Getter du chemin de téléchargement de fichier
	 * @return File path
	 */
	public File getDownloadPath() {
		return pathDownload;
	}
	/**
	 * Setter du chemin de téléchargement de fichier et l'enregistre dans le fichier config.ini
	 * @param file
	 */
	public void setDownloadPath(File file) {
		pathDownload=file;
		//maBD.setDownloadPath(file);
		ini.put("DOWNLOAD", "path", file.getAbsolutePath());
		try {
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Mets à jour la BD, la Vue Principale et la Personne utilisateur du changement de pseudo
	 * @param uname
	 */
	public void setPseudoUserSwitch(String uname) {
		/*maBD.delIdPseudoLink(user.getPseudo());*/
		maBD.setIdPseudoLink(uname,user.getId());
		main.changePseudo(uname);
		user.setPseudo(uname);
		Reseau.getReseau().sendDataBroadcast(Message.Factory.switchPseudoBroadcast(user));	
	}
	/**
	 * Setter de pseudo
	 * @param uname
	 */
	public void setPseudoUserConnexion(String uname) {
		user.setPseudo(uname);
	}
	/** 
	* @param tosend texte à envoyer à activeUser
	 */
	public void sendMessage(String tosend, Interlocuteurs to) {
		Message m =Message.Factory.sendText(tosend.getBytes(), user, to);
		Reseau.getReseau().sendTCP(m);
		main.update(to,m,true);
		maBD.addData(m);
	}
	/**
	 * 
	 * @param file fichier à envoyer
	 * @param f nom du fichier
	 */
	public void sendMessage(byte[] file, File f, Interlocuteurs to) {		
		Message m =Message.Factory.sendFile(file, user, to,f.getName());//new Message(file, user, to,f.getName());
		Reseau.getReseau().sendTCP(m);
		Message m2 =Message.Factory.sendFile(file, user, to,f.getAbsolutePath());
		main.update(to,m2,true);
		maBD.addFile(m2); 
	}
	/**
	 * Récupère historique des messages stockés dans la BD associé à un interlocuteur donné 
	 * @param activeUser interlocuteur 
	 * @return liste de message historique de la conversation
	 */
	public ArrayList<Message> getHistorique(Interlocuteurs activeUser) {
			return maBD.getHistorique(user,activeUser,(activeUser.getInterlocuteurs().size()>1));
	}
	/**
	 * Crée un groupe dans la BD
	 * @param array
	 * @return
	 */
	public boolean creationGroupe(ArrayList<Interlocuteurs> array) {
		array.add(user);
		Group g=new Group(array);
		if(!model.contains(g)) {
		model.add(1,g);
		maBD.addGroup(g.getId(),array);
		
		///TODO avertir les autres de la création du groupe
		Reseau.getReseau().sendTCP(Message.Factory.createGroupe(user, g));
	    // g.removeInterlocuteur(user);
		return true;
		}
		else {
		return false;	
		}
	}	
}
