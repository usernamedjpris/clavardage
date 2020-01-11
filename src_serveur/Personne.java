package com.octest.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Personne implements Serializable{

	private static final long serialVersionUID = -5717914954973757973L;
	private InetAddress adresse;
	private int port;
	private String pseudo;
	private boolean connected;
	private long id;
	/**
	 * @param adresse
	 * @param port
	 * @param pseudo
	 * @param id
	 * @param connected 
	 */
	public Personne(InetAddress adresse, int port, String pseudo, boolean c, long id) {
		this.adresse=adresse;
		this.port=port;
		this.pseudo=pseudo;
		connected=c;
		this.id=id;
	}
	public boolean getConnected() {return connected;}
	public String getPseudo() {
		return pseudo;
	}
	public InetAddress getAdresse() {
		return adresse;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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
	public static byte[] serialize(Personne p) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(p);
	    return out.toByteArray();
	}
	public static Personne deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (Personne) is.readObject();
	}
}
