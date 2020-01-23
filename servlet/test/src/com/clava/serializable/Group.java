package com.clava.serializable;

import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class Group implements Interlocuteurs {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Interlocuteurs> p;
	
	public Group(ArrayList<Interlocuteurs> liste){
		System.out.print("liste :"+liste);
		p=new ArrayList<>(liste);
	}

	@Override
	public boolean getConnected() {
		for (Interlocuteurs c :p){
			if(!c.getConnected()) 
			return false;
		}
		return true; 
	}

	@Override
	public String getPseudo() {
		String pseudo="✌";
		for (Interlocuteurs c :p){
			pseudo+=c.getPseudo()+":";
		}
		return pseudo;
	}

	@Override
	public ArrayList<SimpleEntry<InetAddress, Integer>> getAddressAndPorts() {
		ArrayList<SimpleEntry<InetAddress, Integer>> array=new ArrayList<SimpleEntry<InetAddress, Integer>>();
		for (Interlocuteurs c :p){
			array.addAll(c.getAddressAndPorts());
		}
		return array;
	}
	

	@Override
	public int getId() {
		String id="";
		System.out.print("size :"+p.size()+" p :"+p.toString());
		for (Interlocuteurs c :p){
			id+=c.getId();
		}
		return id.hashCode();
	}
	
	@Override
	public void setConnected(boolean b) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}

	@Override
	public void setPseudo(String nouveauPseudo) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}

	@Override
	public void setPort(int port) throws NoSuchMethodException {
		throw new NoSuchMethodException();
		
	}

	@Override
	public void setAddressAndPorts(SimpleEntry<InetAddress, Integer> a) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}

	@Override
	public ArrayList<Interlocuteurs> getInterlocuteurs() {
		return p;
	}

	public void addInterlocuteur(Interlocuteurs emetteur) {
		p.add(emetteur);		
	}

	public void removeInterlocuteur(Personne user) {
	p.remove(user);
		
	}

	
	

}
