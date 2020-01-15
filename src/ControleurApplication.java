
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ProxySelector;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

//tips: ctrl +r =run (me) ctrl+F11 (standard)
//ctrl+maj+F11=code coverage (standard)
//ObjectAid UML (retro URL)
@SuppressWarnings("deprecation")
//public class ControleurApplication implements Observer {
public class ControleurApplication implements PropertyChangeListener{
	private Personne user;
	private VuePrincipale main;
	private BD maBD=BD.getBD();
	private DefaultListModel<Personne> model = new DefaultListModel<>();
	private File pathDownload;
	private Boolean initialized=false;
	private String pseudoWaiting="";
	private boolean answerPseudo;
	private Object mutex = new Object();
	Wini ini;
	private InetAddress localIp;
	public static void main(String[] args) {
			new ControleurApplication();

	}
	InetAddress findIp() {
		InetAddress localIp;
		try {
					DatagramSocket so=new DatagramSocket();
					so.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 0);
					localIp=so.getLocalAddress();
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
	void init() {
		InetAddress ipServer=null;
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
				try {
					ipServer =InetAddress.getByName(ini.get("IP", "publicServerIp", String.class));
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, " L'adresse IP fournie pour le serveur public dans config.ini n'est pas au format correct,"+
				" vérifiez votre saisie ou supprimez ce champs", "Web Server", JOptionPane.ERROR_MESSAGE);	
					System.exit(0);
				}
				try {
					String s=ini.get("ADVANCED", "doNotUseAutoIpAndUseThisOne", String.class);
					if(!s.equals("")) {
						forceUseIp=true;
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
		user= new Personne(localIp, portTcp,"moi",true,(long)mac.hashCode()); //fixe par poste (adresse mac by eg)
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	void test() {
		 HttpClient client = HttpClient.newBuilder()
			        .version(Version.HTTP_1_1)
			        .followRedirects(Redirect.NORMAL)
			        .connectTimeout(Duration.ofSeconds(20))
			        .proxy(ProxySelector.of(new InetSocketAddress("localhost", 8080)))
			        .build();
		 HttpRequest request;
		try {
			request = HttpRequest.newBuilder()
				        .uri(URI.create("https://foo.com/"))
				        .timeout(Duration.ofMinutes(2))
				        .header("Content-Type", "application/json")
				        .POST(BodyPublishers.ofByteArray(Message.serialize(Message.Factory.askPseudoOkBroadcast(user))))
				        .build();
			   HttpResponse<byte[]> response;
				response = client.send(request, BodyHandlers.ofByteArray());
			
			   System.out.println(response.statusCode());
			   System.out.println(response.body());  
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			  /* client.sendAsync(request, BodyHandlers.ofString())
			        .thenApply(HttpResponse::body)
			        .thenAccept(System.out::println); */
	}
	ControleurApplication(){
		init();
		test();
	    //on récupère les gens avec qui on a déjà parlé #offline reading
	   for(Personne p: maBD.getPseudoTalked(user.getId())) {
		   model.addElement(p);
	   }
	     Reseau.getReseau().sendDataBroadcast(Message.Factory.whoIsAliveBroadcast(user));
	    new VueChoixPseudo(this,false);
	    main=new VuePrincipale(this,model);
	    initialized=true;
	    Reseau.getReseau().sendDataBroadcast(Message.Factory.userConnectedBroadcast(user));
	    model.addElement(user);
	
	}
	String getPseudo() {
		return user.getPseudo();
	}
	Personne getPersonne() {
		return user;
	}
	void sendActiveUserPseudo(Personne to) {
			Reseau.getReseau().sendUDP(Message.Factory.userIsAlive(user, to));
	}
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
			Personne v=(Personne) i;
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
	void deconnexion(String pseudo) {
			Reseau.getReseau().sendDataBroadcast(Message.Factory.userDisconnectedBroadcast(user));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		//try convert arg to message
		/* si  IMAGE to write file :
		 * byte[] encoded = key.getEncoded();
FileOutputStream output = new FileOutputStream(new File("target-file"));
IOUtils.write(encoded, output);
		 */
		//on ne repond pas tant que l'on n'est pas initialis� (avec un pseudo)
		  if (evt.getNewValue() instanceof Message) {
	           Message message = (Message) evt.getNewValue();
	         //do not reply to yourself broadcast ^^ //DEFAULT => possibilité de se parler à soi-même ONLY FOR TEST (simple send en prod)
        	   if(message.getEmetteur().getId()!= user.getId() || message.getType()==Message.Type.DEFAULT || message.getType()==Message.Type.FILE) {
	           System.out.print("\n Reception de :"+message.getType().toString()+" de la part de "+message.getEmetteur().getPseudo()+
	        		   "("+message.getEmetteur().getAdresse().toString()+")"+"\n" );
	           if(message.getType()==Message.Type.DEFAULT) {
	        	   main.update(message.getEmetteur(),message,false);
		           maBD.addData(message); //SAVE BD LE MESSAGE RECU
		          // maBD.printMessage();
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
	        	   main.update(message.getEmetteur(),message,false);
		           maBD.addFile(message); //SAVE BD LE MESSAGE RECU
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
	        	   for(Object ob: model.toArray()) {
	        		   Personne p =(Personne)ob;
	        			   if(p.getId()==message.getEmetteur().getId()) {
	        			   found=true;
	        			   p.setConnected(true);
	        			   p.setPseudo(message.getEmetteur().getPseudo());
			        	   p.setInetAdress(message.getEmetteur().getAdresse());
			        	   p.setPort(message.getEmetteur().getPort());
	        			   break;
	        		   }
	        	   }
		        	  if(!found) {
		        	   model.add(0, message.getEmetteur());
		        	  }
		        	  maBD.setIdPseudoLink(message.getEmetteur().getPseudo(), message.getEmetteur().getId());
		        	  if(initialized)
		        	  main.updateList();
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
	public void sendDisconnected() {
			Reseau.getReseau().sendDataBroadcast(Message.Factory.userDisconnectedBroadcast(user));
	}
	public File getDownloadPath() {
		return pathDownload;
	}
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
	public void setPseudoUserSwitch(String uname) {
		/*maBD.delIdPseudoLink(user.getPseudo());*/
		maBD.setIdPseudoLink(uname,user.getId());
		main.changePseudo(uname);
		user.setPseudo(uname);
		Reseau.getReseau().sendDataBroadcast(Message.Factory.switchPseudoBroadcast(user));
	
	}
	public void setPseudoUserConnexion(String uname) {
		user.setPseudo(uname);
	}
	/** 
	* @param tosend texte à envoyer à activeUser
	 */
	public void sendMessage(String tosend, Personne to) {
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
	public void sendMessage(byte[] file, File f, Personne to) {
		
		Message m =Message.Factory.sendFile(file, user, to,f.getName());//new Message(file, user, to,f.getName());
		Reseau.getReseau().sendTCP(m);
		Message m2 =Message.Factory.sendFile(file, user, to,f.getAbsolutePath());
		main.update(to,m2,true);
		maBD.addFile(m2); 
	}
	public ArrayList<Message> getHistorique(Personne to) {
			return maBD.getHistorique(user,to);
	}
	

}
