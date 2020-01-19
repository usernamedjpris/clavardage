package com.sdzee.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.clava.serializable.Message;
import com.clava.serializable.Personne;
//https://openclassrooms.com/fr/courses/626954-creez-votre-application-web-avec-java-ee/619584-la-servlet
//add  <Context docBase="test" path="/test" reloadable="true" source="org.eclipse.jst.jee.server:test"/></Host>
//to server.xml
public class Test extends HttpServlet {
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10 ko
	private ArrayList<Personne> disponibilite = new ArrayList<Personne>();
	private Personne serveurDePresence = new Personne(null, 8005, "Serveur de presence", true, 2L);
	// on peut mettre aussi inetAdress getlocalhost mais après UnknownHostException e a gerer
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   // String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
	   //Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	   // String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
	   try {
		   InputStream fileContent = request.getInputStream();
		   
		   byte[] decodedBytes = Base64.getDecoder().decode(fileContent.readAllBytes());
		   ByteArrayInputStream m3 =new ByteArrayInputStream(decodedBytes);
		   DataInputStream dis = new DataInputStream(m3);
	       int len = dis.readInt();
	       byte[] data = new byte[len];
	       if (len > 0) {
	           dis.read(data, 0,len);
	        }
		    System.out.print(data);
	   		Message message = Message.deserialize(data);
			Personne p = message.getEmetteur();			
			
			//recuperation du couple (adresse,port) du NAT pour du tcp hole punching
			InetAddress addrNat = InetAddress.getByName(request.getRemoteAddr());		
			int portNat = request.getRemotePort();
			
			p.setInetAdress(addrNat);
			p.setPort(portNat);
			
			if (message.getType()==Message.Type.SWITCH) {
	        	updateSwitch(p);
	        }
	        else if (message.getType()==Message.Type.CONNECTION) {
	        	updateConnection(p);
	        }	        
	        else if (message.getType()==Message.Type.DECONNECTION) {
	        	updateDeconnection(p);
	        }
	        else if (message.getType()==Message.Type.ASKPSEUDO) {
	        	Personne samePseudo = personneWithPseudo(p.getPseudo());
	        	if(samePseudo!=null) {
	        		Message messageReplied = Message.Factory.usernameAlreaydTaken(samePseudo, p);
	        	    repondMessage(messageReplied, response);
	        	}
	        }	
	        else if (message.getType()==Message.Type.WHOISALIVE) {
	        	ArrayList<Personne> liste = getWhoisalive();	        	       	
	        	for (int i=0;i<liste.size();i++) { //on envoie autant de message ALIVE que de Persone who are alive	 
	        		Message messageReplied = Message.Factory.userIsAlive(liste.get(i), p);
	                repondMessage(messageReplied, response);
	        	}
	        }
		    
		    // System.out.print(m.getType()); 
		    /*
		    Message r=Message.Factory.sendText("hello nice !".getBytes(),new Personne(null, 0, null, false, 0),	m.getEmetteur());
		    
		    byte[] rep=Message.serialize(r);
		    
		    response.reset();
			response.setBufferSize( DEFAULT_BUFFER_SIZE );
			String  type = "application/octet-stream";
			response.setContentType( type );
			response.setHeader( "Content-Length", ""+rep.length );
			BufferedOutputStream sortie = null;
			try {
			    // Ouvre les flux 
			    // entree = new BufferedInputStream( new FileInputStream( fichier ), TAILLE_TAMPON );
			    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
			    sortie.write( rep, 0, rep.length );
			} finally {
			    try {
			        sortie.close();
			    } catch ( IOException ignore ) {
			    }
			}*/
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	}
	/**
	 * Repond au clientHTTP un message
	 * @param message a envoyer
	 */
	private void repondMessage(Message m, HttpServletResponse response ) {	
		byte[] rep = Message.serialize(m);
	    response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		String  type = "application/octet-stream";
		response.setContentType( type );
		response.setHeader( "Content-Length", ""+rep.length );
		BufferedOutputStream sortie = null;
		try {
		    /* Ouvre les flux */
		    sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
		    sortie.write( rep, 0, rep.length );
		} finally {
		    try {
		        sortie.close();
		    } catch ( IOException ignore ) {
		    }
		}
	}
	
	/**
	 * Trouve l'index de la Personne ayant l'id donné
	 * @param id de la Personne recherchee
	 * @return indexOf(id) ; -1 si inexistante
	 */
	private int findIDfromDisponibilite(long id) { 
		boolean found = false;
		int i=0;
		int index = -1;
		while (i < disponibilite.size() && !found) {
			if (disponibilite.get(i).getId()==id) {
				index = i;
				found = true;					
			}
			i++;
		}
		return index;
	}
	/**
	 * Recherche parmi disponibilite une personne ayant le pseudo donné
	 * @param pseudo de la personne recherchee
	 * @return Personne avec ce pseudo si déjà pris, null sinon
	 */
	private Personne personneWithPseudo(String pseudo) { 
		boolean found = false;
		Personne p = null;
		int i=0;
		while (i < disponibilite.size() && !found) {
			if (disponibilite.get(i).getPseudo().equals(pseudo)) {
				p = disponibilite.get(i);
				found = true;	 
			}
			i++;
		}
		return p;
	}
	/**
	 * opere le changement de pseudo avec le nouveau pseudo fourni. 
	 * <p>Repère la Personne grâce à son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param Personne p
	 */
	private void updateSwitch(Personne p) {
		int index = findIDfromDisponibilite(p.getId());
		if (index>=0) { //la personne est dans disponibilite
			disponibilite.get(index).setPseudo(p.getPseudo());
		}
		else {
			disponibilite.add(p);
		}
	}
	/**
	 * met à jour de l'état de connexion à true dans liste de disponibilité 
	 * <p>Repère la Personne grâce à son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param Personne p 
	 */
	private void updateConnection(Personne p) {
		int index = findIDfromDisponibilite(p.getId());
		if (index>=0) { //la personne est dans disponibilite
			disponibilite.get(index).setConnected(true);
		}
		else {
			p.setConnected(true);
			disponibilite.add(p);
		}
	}
	/**
	 * met à jour de l'état de connexion à false dans liste de disponibilité 
	 * <p>Repère la Personne grâce à son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param Personne p
	 */
	private void updateDeconnection(Personne p) {
		int index = findIDfromDisponibilite(p.getId());
		if (index>=0) { //la personne est dans disponibilite
			disponibilite.get(index).setConnected(false);
		}
		else {
			p.setConnected(false);
			disponibilite.add(p);
		}
	}
	/**
	 * Donne la liste des personnes connectées
	 * @return ArrayList des personnes de disponibilite avec connected=true
    */
	private ArrayList<Personne> getWhoisalive() {
		ArrayList<Personne> Liste = new ArrayList<Personne>();
		for (int i=0; i< disponibilite.size();i++) {
			Personne p = disponibilite.get(i);
			if (p.getConnected()) {
				Liste.add(p);
			}			
		}
		return Liste;
	}
}