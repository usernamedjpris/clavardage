package com.clava.serializable;

import java.io.Serializable;
import java.net.*;

public class Personne implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InetAddress adresse;
	private int port;//tcp, UDP étant en broadcast, il faut le même pour toutes les personnes (dans config.ini), inutile de charger personne avec un attribut static
	private String pseudo;
	private boolean connected;
	private long id;
	/**
	 * @param adresse
	 * @param portTCP 
	 * @param pseudo
	 * @param connected 
	 * @param identifiant
	 */
	public Personne(InetAddress adresse,int port, String pseudo,boolean c, long id) {
		this.adresse=adresse;
		this.pseudo = pseudo;
		connected=c;
		this.id=id;
		this.setPort(port);
	}
	
	public boolean getConnected() {return connected;}
	public String getPseudo() {
		return pseudo;
	}
	public InetAddress getAdresse() {
		return adresse;
	}
	public void setPseudo(String nouveauPseudo) {
		pseudo=nouveauPseudo;
	}
	public void setInetAdress(InetAddress a) {
		adresse=a;
	}
	public void setConnected(boolean b) {
		connected=b;
		
	}
	public long getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
