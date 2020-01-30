package com.clava.serializable;

import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Group implements Interlocuteurs {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Interlocuteurs> p;
	private int id;
	
	@Override
    public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group g = (Group) o;
        return g.getId()==this.getId();
	}
	@Override
    public int hashCode() {
        return getId();
    }
	
	public Group(ArrayList<Interlocuteurs> liste){
		//System.out.print("liste :"+liste);
		p=new ArrayList<>(liste);
		ArrayList<Integer> array =new ArrayList<>();
		for(Interlocuteurs i: p) {
			array.add(i.getId());
		}
		 Collections.sort(array);
		 String id="";
		 for(int i:array) {
			 id+=i;
		 }
		 this.id=id.hashCode();
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
		return id;
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
