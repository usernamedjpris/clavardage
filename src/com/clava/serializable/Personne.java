package com.clava.serializable;
import java.io.Serializable;
import java.net.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;


public class Personne implements Serializable, Interlocuteurs{

	private static final long serialVersionUID = 1L;
	private SimpleEntry<InetAddress, Integer> adresse;
	/*port tcp, UDP est en broadcast, il faut le meme pour toutes les personnes 
	(dans config.ini), inutile de charger personne avec un attribut static */
	private String pseudo;
	private boolean connected;
	private int id;
	/**
	 * @param a adresse et portTcp
	 * @param pseudo
	 * @param connected 
	 * @param identifiant
	 */
	public Personne(SimpleEntry<InetAddress, Integer> a, String pseudo,boolean c, int id) {
		this.adresse=a;
		this.pseudo = pseudo;
		connected=c;
		this.id=id;
	}
	@Override
    public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne p = (Personne) o;
        return p.getId()==this.getId();
	}
	@Override
    public int hashCode() {
        return getId();
    }
	
	@Override
	public boolean getConnected() {return connected;}
	@Override
	public String getPseudo() {
		return pseudo;
	}
	@Override
	public void setPseudo(String nouveauPseudo) {
		pseudo=nouveauPseudo;
	}
	@Override
	public void setConnected(boolean b) {
		connected=b;
	}
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setPort(int port) {
		adresse.setValue(port);
	}

	@Override
	public ArrayList<SimpleEntry<InetAddress, Integer>> getAddressAndPorts() {
		ArrayList<SimpleEntry<InetAddress, Integer>> array=new ArrayList<>();
		array.add(adresse);
		return array;
	}

	@Override
	public void setAddressAndPorts(SimpleEntry<InetAddress, Integer> a) throws NoSuchMethodException {
		this.adresse=a;
		
	}

	@Override
	public ArrayList<Interlocuteurs> getInterlocuteurs() {
		 ArrayList<Interlocuteurs> array= new ArrayList<Interlocuteurs>();
		 array.add(this);
		return array;
	}



	

}
