package com.clava.model.reseau;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import javax.swing.JOptionPane;

import com.clava.serializable.Message;
/**
 * ClientHTTP permet l'envoi et la reception (observable) de messages avec le protocole HTTP 
 */
public class ClientHTTP implements Runnable {
	private String ipServer;
	private int portServer;
	private HttpClient client;
	private Message message;
	private PropertyChangeSupport support;
	/**
	 * constructeur ClientHTTP 
	 * <p>[Design Pattern Observers]</p>
	 * @param ipServer
	 * @param portServer
	 */
    public ClientHTTP(String ipServer, int portServer) {
		this.ipServer = ipServer;
		this.portServer = portServer;
		client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.NEVER)
			      .build();
		support = new PropertyChangeSupport(this);
		
	}
	 /**
     * Remonte la réponse du serveur HTTP à la classe Reseau [Design Pattern Observers]
     * @param pcl Objet qui implémente PropertyChangeListener (à notifier)
     * @see Reseau
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
    /**
     * Envoie un messge m en HTTP 
     * @param m Message à envoyer
     */
	public void sendMessage (Message m) { //String data, Personne dest, Personne emmet //https://www.baeldung.com/java-http-request
		//encodage inutile (cf reponse au retour non encode, taille de l'envoi àvoir si utile ou pas)
		message=m;
		//en bloquant si deconnexion (laisse le temps d'envoyer le message avant de kill
		if(message.getType()!=Message.Type.DECONNECTION) {
			Thread tu = new Thread(this);
        	tu.start();
		} else
			run();

	}
	/**
	 * Le thread clientHTTP (implements Runnable) soumet une requête HTTP (avec timeout de 5s) au serveur de présence 
	 * @see ControleurApplication#configServeur()
	 */
	@Override
	public void run() {
		
		HttpRequest request;
		try {
		byte[] m= Message.serialize(message);
		System.out.print("http://"+ipServer+":"+portServer+"/test/clavardeur");
		request = HttpRequest.newBuilder()
			      .uri(URI.create("http://"+ipServer+":"+portServer+"/test/clavardeur"))
			      .timeout(Duration.ofMillis(5000)) 
			      .header("Content-Type", "application/octet-stream")
			      .POST(BodyPublishers.ofByteArray(m))
			      .build();
		
		 HttpResponse<byte[]> response  = client.send(request, BodyHandlers.ofByteArray());
		 
		 Message rep=Message.deserialize(response.body());
		 support.firePropertyChange("serveur","", rep);
		 
		 System.out.println("\n reponse serveur : " +rep.getType());//+" personne(s) : "+rep.getEmetteur().getPseudo());
			} catch (InterruptedException e) {
				e.printStackTrace();
			/*} catch (ClassNotFoundException e) {
				e.printStackTrace();*/
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, " La combinaison adresse IP/port fournie pour le serveur public dans config.ini n'est pas au format correct,"+
						" vérifiez votre saisie ", "Web Server", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}catch (ConnectException e) {
				JOptionPane.showMessageDialog(null, " La combinaison adresse IP/port fournie pour le serveur public dans config.ini n'est pas joignable,"+
						" vérifiez votre saisie", "Web Server", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
}
