package com.octest.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Serveur
 */
@WebServlet("/Serveur")
public class Serveur extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<Personne> disponibilite;
	private Personne serveurDePresence;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Serveur() {
        super();
        disponibilite = new ArrayList<Personne>();
        try {
			serveurDePresence = new Personne(InetAddress.getLocalHost(), 8005, "Serveur de presence", true, 2L);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * doGet http
     * structure attendue : body : Message (type, Personne serialisee) serialise
     */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//recuperation du body de la request
		int len = request.getContentLength();
        byte[] data = new byte[len];
        if (len > 0) {
        	request.getInputStream().read(data);
        }
        try {
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
	        		Message messageReplied = Message.Factory.usernameAlreaydTaken(serveurDePresence, samePseudo);
	        		byte[] buf = Message.serialize(messageReplied);
	                response.getOutputStream().write(buf);	                
	                response.getOutputStream().flush();  // commit response
	        	}
	        }	
	        else if (message.getType()==Message.Type.WHOISALIVE) {
	        	ArrayList<Personne> liste = getWhoisalive();
	        	//serialization de la liste
	            try
	            {
	                //serialize list to a byte array : https://stackoverflow.com/questions/23793885/how-to-serialize-arraylist-of-objects
	                ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
	                ObjectOutputStream out = new ObjectOutputStream(bos) ;
	                out.writeObject(liste);
	                out.close();
	                byte[] buf = bos.toByteArray();
	                
	                response.getOutputStream().write(buf);	                
	                response.getOutputStream().flush();  // commit response
	            } 
	            catch (IOException ioe) 
	            {
	                ioe.printStackTrace();
	            }
	        } 	   	        
	        
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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