package com.octest.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Serveur() {
        super();
        disponibilite = new ArrayList<Personne>();
    }

    /**
     * structure attendue : parametre "typeOfRequest: CONNECTION|SWITCH|DECONNECTION|WHOISALIVE" et body : Personne serialisee
     */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//recuperation du body de la request
		int len = request.getContentLength();
        byte[] data = new byte[len];
        if (len > 0) {
        	request.getInputStream().read(data);
        }
        try {
			Personne p = Personne.deserialize(data);
			
	        //recuperation du parametre de la request
	        String type = request.getParameter("typeOfRequest");

	        if (type.equals("SWITCH")) {
	        	updateSwitch(p);
	        }
	        else if (type.equals("CONNECTION")) {
	        	updateConnection(p);
	        }	        
	        else if (type.equals("DECONNECTION")) {
	        	updateDeconnection(p);
	        }
	        else if (type.equals("WHOISALIVE")) {
	        	ArrayList<Personne> liste = getWhoisalive();
	        	//serialization de la liste
	            try
	            {
	                // Serialize liste to a byte array : https://stackoverflow.com/questions/23793885/how-to-serialize-arraylist-of-objects
	                ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
	                ObjectOutputStream out = new ObjectOutputStream(bos) ;
	                out.writeObject(liste);
	                out.close();
	                byte[] buf = bos.toByteArray();
	                response.getOutputStream().write(buf);
	                // TODO send response to the right person 
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
	//tests
/*	public static void main (String[] args){
		Serveur t1 = new Serveur();
		t1.disponibilite.add(new Personne(null,10,"toto", true, 1L));
		t1.disponibilite.add(new Personne(null,11,"tutu", true, 2L));
		t1.disponibilite.add(new Personne(null,12,"tintin", true, 3L));
		
		System.out.println("disponibilite");
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("postSwitch");
		t1.updateSwitch(new Personne(null,10,"titi", true, 2L));
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("postSwitch not found");
		t1.updateSwitch(new Personne(null,10,"titin", true, 5L));
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("postDeconnected");
		t1.updateDeconnection(new Personne(null,10,"titi", true, 2L));
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("postConnected");
		t1.updateConnection(new Personne(null,10,"titi", true, 2L));
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("postDeconnected");
		t1.updateDeconnection(new Personne(null,10,"tititzu", true, 7L));
		t1.printListePersonne(t1.disponibilite);
		
		System.out.println("getWhoisalive");
		ArrayList<Personne> liste = t1.getWhoisalive();
		t1.printListePersonne(liste);
	}*/

	/**
	 * indexOf mais sur l'attribut id seulement et pas sur toute la classe Personne
	 * @param id de la personne recherchee
	 * @return indexOf(id)
	 */
	private int findIDfromDisponibilite(long id) { 
		boolean found = false;
		int i=0;
		int index = -1;
		while (i < disponibilite.size() && !found) {
			if (disponibilite.get(i).getId()==id) {
				found = true;	
				index = i;
			}
			i++;
		}
		return index;
	}
	/**
	 * opere le changement de pseudo avec le nouveau pseudo fourni. 
	 * <p>Repère la Personne grâce à son id dans la liste ou l'ajoute si non trouvee.</p>
	 * @param p
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
	 * @param p
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
	 * @param p
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
	
	//pour tests
	private void printListePersonne(ArrayList<Personne> l) {
		for (int i=0; i< l.size();i++) {
			System.out.println(i+" "+l.get(i).getPseudo()+" connected:"+l.get(i).getConnected()+" @dresse:"+l.get(i).getAdresse()+" port:"+l.get(i).getPort());
		}	
	}
}
