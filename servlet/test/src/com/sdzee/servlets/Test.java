package com.sdzee.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clava.serializable.Group;
import com.clava.serializable.Interlocuteurs;
import com.clava.serializable.Message;
//https://openclassrooms.com/fr/courses/626954-creez-votre-application-web-avec-java-ee/619584-la-servlet
//add  <Context docBase="test" path="/test" reloadable="true" source="org.eclipse.jst.jee.server:test"/></Host>
//to server.xml
public class Test extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ArrayList<Interlocuteurs> disponibilite = new ArrayList<>();
	//private ClientTCP client;
	ArrayList<String> pseudoWaiting=new ArrayList<String>();
	private int DEFAULT_BUFFER_SIZE=2048;
	public Test(){
		//client=new ClientTCP();
	}

	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   // String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
	    //Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	   // String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
	   InputStream fileContent = request.getInputStream();
	   
	   /*byte[] decodedBytes = Base64.getDecoder().decode(fileContent.readAllBytes());
	   ByteArrayInputStream m3 =new ByteArrayInputStream(decodedBytes);
	   DataInputStream dis = new DataInputStream(m3);
       int len = dis.readInt();
       byte[] data = new byte[len];
       if (len > 0) {
           dis.read(data, 0,len);
       }*/
       byte[] data=fileContent.readAllBytes();
			Message m;
			try {
				m = Message.deserialize(data);

			Interlocuteurs p = m.getEmetteur();				
			//recuperation du couple (adresse,port) du NAT pour du tcp hole punching
			InetAddress addrNat = InetAddress.getByName(request.getRemoteAddr());		
			int portNat = request.getRemotePort();
			System.out.print(" \n RECEPTION DE : "+m.getType()+" avec "
					+ "adresse : "+addrNat+" port "+portNat+" pseudo : "+
					m.getEmetteur().getPseudo());
			System.out.print(" \n LIST *users*  :  \n ");
			for(Interlocuteurs i:disponibilite) {
				System.out.print("adresse : "+addrNat+" port "+portNat+" pseudo : "+i.getPseudo()+" \n");
			}
			System.out.print("\n\n");
			//rq: seule 1 personne envoie des messages au serveur
			// (pas un groupe)
			//=> cast to personn possible
			try {
				p.setAddressAndPorts(new SimpleEntry<>(addrNat,portNat));
			} catch (NoSuchMethodException e) {
				System.out.print(" Les groupes ne devraient pas parler au serveur !");
				e.printStackTrace();
			}
			
			if (m.getType()==Message.Type.SWITCH) {
	        	updateSwitch(p);
	        	repondMessage(Message.Factory.okServeur(), response);
	        }
	        else if (m.getType()==Message.Type.CONNECTION) {
	        	updateConnection(p);
	        	pseudoWaiting.remove(p.getPseudo());
	        	repondMessage(Message.Factory.okServeur(), response);
	        }	        
	        else if (m.getType()==Message.Type.DECONNECTION) {
	        	updateDeconnection(p);
	        	repondMessage(Message.Factory.okServeur(), response);
	        }
	        else if (m.getType()==Message.Type.ASKPSEUDO) {
	        	boolean found=false;
	        	for(String s:pseudoWaiting) {
			        	if(s.equals(p.getPseudo())){
			        		System.out.print(pseudoWaiting+" NEINNNNN already used !");
			        		Message messageReplied = Message.Factory.usernameAlreaydTaken(null, p);
			        		found=true;
			        		repondMessage(messageReplied, response);
			        		break;
			        		//client.sendMessage(messageReplied, p); 
			        	}
	        	}
	        	if(!found)
	        		pseudoWaiting.add(p.getPseudo());
	        	
	        	Interlocuteurs samePseudo = personneWithPseudo(p.getPseudo());
	        	if(samePseudo!=null) {
	        		Message messageReplied = Message.Factory.usernameAlreaydTaken(null, p);
	        		//client.sendMessage(messageReplied, p); 
	        		repondMessage(messageReplied, response);
	        	}
	        	repondMessage(Message.Factory.okServeur(), response);
	        }	
	        else if (m.getType()==Message.Type.WHOISALIVE) { 
	        		Message messageReplied = Message.Factory.userIsAlive(new Group(disponibilite), p);
	        		repondMessage(messageReplied, response);
	        }	
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			
			
	}
		
	
	
	/**
	 * Trouve l'index de la Personne ayant l'id donnÃ©
	 * @param id de la Personne recherchee
	 * @return indexOf(id) ; -1 si inexistante
	 */
	private int findIDfromDisponibilite(long id) { 
		int index=-1;
		int tmp=0;
		for(Interlocuteurs i:disponibilite) {
			if(i.getId()==id) {
				index=tmp;
				break;
			}
			tmp++;
		}
		return index;
	}
	/**
	 * Recherche parmi disponibilite une personne ayant le pseudo donnÃ©
	 * @param pseudo de la personne recherchee
	 * @return Personne avec ce pseudo si dÃ©jÃ  pris, null sinon
	 */
	private Interlocuteurs personneWithPseudo(String pseudo) { 
		for(Interlocuteurs i:disponibilite) {
			if(i.getPseudo().equals(pseudo)) {
				return i;
			}
		}
		return null;
	}
	/**
	 * opere le changement de pseudo avec le nouveau pseudo fourni. 
	 * <p>RepÃ¨re la Personne grÃ¢ce Ã  son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param Personne p
	 */
	private void updateSwitch(Interlocuteurs p) {
		int index = findIDfromDisponibilite(p.getId());
		if (index>=0) { //la personne est dans disponibilite
			try {
				disponibilite.get(index).setPseudo(p.getPseudo());
			} catch (NoSuchMethodException e) {
				System.out.print("Les groupes ne changent pas de pseudo tout seul !");
				e.printStackTrace();
			}
		}
		else {
			disponibilite.add(p);
		}
	/*		for(Interlocuteurs o:disponibilite) {
				repondMessage(Message.Factory.switchPseudoBroadcast(p),response);
			//client.sendMessage(Message.Factory.switchPseudoBroadcast(p),o);
			}
			*/
	}
	/**
	 * met Ã  jour de l'Ã©tat de connexion Ã  true dans liste de disponibilitÃ© 
	 * <p>RepÃ¨re la Personne grÃ¢ce Ã  son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param response 
	 * @param Personne p 
	 */
	private void updateConnection(Interlocuteurs p) {
		int index = findIDfromDisponibilite(p.getId());
		if (index<0) {
			disponibilite.add(p);
		}else
			System.out.print("Check your code, not one can connect itself"
					+ " twice without disconnect between both");
		/*try {
			for(Interlocuteurs o:disponibilite)
			client.sendMessage(Message.Factory.userConnectedBroadcast(p),o);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	/**
	 * met Ã  jour de l'Ã©tat de connexion Ã  false dans liste de disponibilitÃ© 
	 * <p>RepÃ¨re la Personne grÃ¢ce Ã  son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param Personne p
	 */
	private void updateDeconnection(Interlocuteurs p) {

			if(!disponibilite.remove(p)) {
				System.out.print(" \n Check updateDecconection, it's not possible to "
						+ "deconnect without first being conected via a CONNEXION");
			}
			/*try {
				for(Interlocuteurs o:disponibilite)
				client.sendMessage(Message.Factory.userDisconnectedBroadcast(p),o);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
	}
	 // Repond au clientHTTP un message
	 // @param message a envoyer
	 	private void repondMessage(Message m, HttpServletResponse response ) {	
		byte[] rep;
		BufferedOutputStream sortie = null;
		try {
			rep = Message.serialize(m);
	    response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		String  type = "application/octet-stream";
		response.setContentType( type );
		response.setHeader( "Content-Length", ""+rep.length );
		    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
		    sortie.write( rep, 0, rep.length );
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
		    	if(sortie != null)
		        sortie.close();
		    } catch ( IOException ignore ) {
		    }
		}
	}

}
/*/**	/**
	 * Donne la liste des personnes connectÃ©es
	 * @return ArrayList des personnes de disponibilite avec connected=true
	private ArrayList<Personne> getWhoIsAlive() {
		ArrayList<Personne> Liste = new ArrayList<Personne>();
		for (int i=0; i< disponibilite.size();i++) {
			Personne p = disponibilite.get(i);
			if (p.getConnected()) {
				Liste.add(p);
			}			
		}
		return Liste;
	}
	
	/*
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		response.setContentType("text/html");
		response.setCharacterEncoding( "UTF-8" );
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"utf-8\" />");
		out.println("<title>Test</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<p>Ceci est une page générée depuis une servlet.</p>");
		out.println("</body>");
		out.println("</html>");
		
		//traitement de la demande
		if(false) {
			// Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas 
		    response.sendError(HttpServletResponse.SC_NOT_FOUND);
		    return;
		}
		// Initialise la réponse HTTP 
		response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		String  type = "application/octet-stream";
		response.setContentType( type );
		String data=" hello jolie fille !";
		response.setHeader( "Content-Length", ""+data.length() );
		BufferedOutputStream sortie = null;
		try {
		   // entree = new BufferedInputStream( new FileInputStream( fichier ), TAILLE_TAMPON );
		    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
		    sortie.write( data.getBytes(), 0, data.length() );
		} finally {
		    try {
		        sortie.close();
		    } catch ( IOException ignore ) {
		    }
		}
	}
	*/