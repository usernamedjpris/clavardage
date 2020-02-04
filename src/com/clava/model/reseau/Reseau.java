package com.clava.model.reseau;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.clava.serializable.Message;

//https://www.baeldung.com/java-observer-pattern
//PropertyChangeListener better (java 11 )


//public class Reseau extends Observable implements Observer {
/**
 * Reseau permet l'envoi de message en utilisant les différent clients à sa disposition (ClientUDP, ClientTCP, ClientHTTP) et permet une réception observable des messages remontés (ClientHTTP, ServeurTCP, ServeurUDP)
 */
public class Reseau implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private ClientHTTP clientHTTP;
	private ServeurTCP serveurTcp;
	private ClientTCP clientTcp;
	private ClientUDP clientUDP;
	private ServeurUDP serveurUDP;
	static Reseau theNetwork;
	/**
	 * Remonte message reçu du reseau par les classes ClientHTTP ou ServeurUDP ou encore ServeurTCP à la classe ControlleurApplication [Design Pattern Observers]
	 * @see ControleurApplication
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
    /**
     * Constructeur Reseau privé
     * <p>
     * [Design Pattern Singleton, Observers]
     * </p>
     * 
     * @see Reseau#getReseau()
     */
	private Reseau() {
	}
	/**
	 * Décide de créer un Reseau en fonction de s'il en existe un déjà (classe singleton)
	 * @see Reseau#theNetwork
	 * @return
	 */
	public static Reseau getReseau() {
		if (theNetwork == null) {
				theNetwork = new Reseau();
		}
		return theNetwork;
	}
	/**
	 * initialise les differents protocoles utilisés : UDP, TCP, HTTP
	 * @param portTCP
	 * @param portUDP
	 * @param ipServer
	 * @param portServer
	 */	
	public void init(int portTCP, int portUDP, String ipServer, int portServer) {
		support = new PropertyChangeSupport(this);
		this.serveurTcp = new ServeurTCP(portTCP);
		this.serveurTcp.addPropertyChangeListener(this);

		Thread tr = new Thread(serveurTcp);
        tr.start();
		this.serveurUDP = new ServeurUDP(portUDP);
		this.serveurUDP.addPropertyChangeListener(this);
		Thread tu = new Thread(serveurUDP);
        tu.start();
        
        this.clientHTTP=new ClientHTTP(ipServer, portServer);
        clientHTTP.addPropertyChangeListener(this);
		this.clientTcp = new ClientTCP();//on get auto adresse +port dans personne destinataire (get from serveur/UDP #discovery part)
		this.clientUDP = new ClientUDP(portUDP);//port nécessaire pour broadcast, #same config UDP everywhere
	}
	/**
	 * Utilise ClientTCP pour envoyer en TCP un message au destinataire indiqué dans le message
	 * @param message à envoyer
	 */
	public void sendTCP(Message message) {
		try {
			System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi le message "+message.getType().toString()+" en tcp ("
		+message.getDestinataire().getAddressAndPorts().toString());
			clientTcp.sendMessage(message);
		} catch (IOException e) {
			//warning graphique envoi fail
			JOptionPane.showMessageDialog(null, "Erreur réseau à l'envoi du message :'( ", "Erreur ", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}
	}
	/**
	 * Utilise ClientHTTP pour envoyer un message en Http au serveur de présence
	 * @param message à envoyer
	 */	
	public void sendHttp(Message m) {
		System.out.print("\n"+m.getEmetteur().getPseudo()+" envoi d'un message "+m.getType().toString()+" au serveur ");
		clientHTTP.sendMessage(m);
	}
	/**
	 * Utilise ClientUDP pour envoyer un message en UDP en broadcast 
	 * @param message à envoyer du type DECONNECTION, SWITCH, CONNECTION, WHOISALIVE, ASKPSEUDO, ou GROUPCREATION
	 */	
	public void sendDataBroadcast(Message message) {
		try {			
			clientUDP.broadcast(message);
			sendHttp(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Utilise ClientUDP pour envoyer en UDP un message au destinataire indiqué dans le message
	 * @param message à envoyer du type REPLYPSEUDO ou ALIVE
	 */
	public void sendUDP(Message message) {
		System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi d'un message "+message.getType().toString()+" à "+
	message.getDestinataire().getPseudo()+"("+message.getDestinataire().getAddressAndPorts().toString());
		try {
		clientUDP.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Reçoit message [Design Pattern Observers] et le transmet directement par le même moyen au ControlleurApplication
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}
}