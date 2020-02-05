package com.clava.serializable;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
/*AbstractMap.SimpleImmutableEntry<Integer, String> entry
= new AbstractMap.SimpleImmutableEntry<>(1, "one");*/
/**
 * l'interface Interlocuteurs est implémenté soit par un Groupe soit par une Personne 
 * <p>L'écriture des messages reste donc inchangée que ce soit par un Groupe ou une Personne</p>
 */
public interface Interlocuteurs extends Serializable{

	boolean getConnected(); //if all ok 

	String getPseudo(); //Somme 

	ArrayList<SimpleEntry<InetAddress, Integer>> getAddressAndPorts();
	
	ArrayList<Interlocuteurs> getInterlocuteurs();
	
	int getId(); //somme
	
	/* Leaf specific */ 
	void setAddressAndPorts(SimpleEntry<InetAddress, Integer> a) throws NoSuchMethodException;
	
	void setConnected(boolean b) throws NoSuchMethodException;
	
	void setPseudo(String nouveauPseudo) throws NoSuchMethodException ;

	void setPort(int port) throws NoSuchMethodException;
	
	@Override
    public boolean equals(Object o);
	
	@Override
    public int hashCode();

}