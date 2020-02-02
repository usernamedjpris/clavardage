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
public class Reseau implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private ClientHTTP clientHTTP;
	private ServeurTCP serveurTcp;
	private ClientTCP clientTcp;
	private ClientUDP clientUDP;
	private ServeurUDP serveurUDP;
	static Reseau theNetwork;
	/**
	 * @param serveurTcp
	 * @param clientTcp
	 * @param clientUDP
	 * @throws IOException
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

	private Reseau() {
	}
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

	public static Reseau getReseau() {
		if (theNetwork == null) {
				theNetwork = new Reseau();
		}
		return theNetwork;
	}
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
	public void sendHttp(Message m) {
		System.out.print("\n"+m.getEmetteur().getPseudo()+" envoi d'un message "+m.getType().toString()+" au serveur ");
		clientHTTP.sendMessage(m);
	}
	public void sendDataBroadcast(Message message) {
		try {			
			clientUDP.broadcast(message);
			sendHttp(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendUDP(Message message) {
		System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi d'un message "+message.getType().toString()+" à "+
	message.getDestinataire().getPseudo()+"("+message.getDestinataire().getAddressAndPorts().toString());
		try {
		clientUDP.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}
}