package com.clava.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
/*AbstractMap.SimpleImmutableEntry<Integer, String> entry
= new AbstractMap.SimpleImmutableEntry<>(1, "one");*/

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

}