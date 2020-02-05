package com.clava.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/* limit serialization / when write/redef java methods :
 * You have seen while deserializing the object the values of a and b has changed.
 * The reason being a was marked as transient and b was static.
In case of transient variables:-
A variable defined with transient keyword is not serialized during serialization process.
This variable will be initialized with default value during deserialization. (e.g: for objects it is null, for int it is 0).
In case of static Variables:-
A variable defined with static keyword is not serialized during serialization process.
This variable will be loaded with current value defined in the class during deserialization.
*/
/**
 * Message est une classe qui encapsule toutes les informations que l'on a besoin de partager par le réseau (d'où le besoin d'être sérializable)
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	public enum Type {CONNECTION, DECONNECTION, SWITCH, WHOISALIVE, ALIVE, ASKPSEUDO, REPLYPSEUDO, FILE, GROUPCREATION, OKSERVEUR, DEFAULT}
	private byte[] data;
	private Interlocuteurs emetteur;
	private Interlocuteurs destinataire;
	private Date date;
	private Type t;
	private String nameFile;
	
	/**
	 * Message à une Interlocuteurs TCP ou UDP (si réponse broadcast)
	 * <p> [Design Pattern Serialization]</p>
	 * @param cat type de message entre ALIVE, REPLYPSEUDO, DEFAULT
	 * @param data texte si DEFAULT, null sinon
	 * @param emetteur
	 * @param destinataire
	 */
	private Message(Type cat,byte[] data, Interlocuteurs emetteur, Interlocuteurs destinataire) {
		this.data = data;
		this.emetteur = emetteur;
		this.destinataire=(destinataire);
		this.date = new Date();
		this.t=cat;
		this.nameFile = "";
	}
	
	/**
	 * Broadcast Message UDP
	 * <p> [Design Pattern Serialization]</p>
	 * @param cat type entre SWITCH, CONNEXION, DECONNEXION, WHOISALIVE, ASKPSEUDO
	 * @param Interlocuteurs c-a-d l'emetteur
	 * <br> rq pour SWITCH le nouveau pseudo est dans l'emetteur( check les id et maj en reception)
	 * <br> idem pour ASKPSEUDO
	 */
	private Message(Type cat, Interlocuteurs Interlocuteurs) {
		destinataire=null;
		emetteur=Interlocuteurs;
		t=cat;
		date=new Date();
	}
	/**
	 * All inclusive builder, for FILE or BD 
     * <p> [Design Pattern Serialization]</p>
	 * @param bytes
	 * @param typ
	 * @param emet
	 * @param interlocuteur
	 * @param date2 date d'envoi
	 * @param name nom du fichier
	 */
	private Message(Type typ,byte[] bytes, Interlocuteurs emet, Interlocuteurs interlocuteur, Date date2,String name) {
		data=bytes;
		emetteur=emet;
		destinataire=interlocuteur;
		date=date2;
		t=typ;
		nameFile=name;
	}
	
	public static class Factory {
		/**
		 * @param data texte à transmettre
		 * @param emetteur l'utilisateur
		 * @param destinataire l'Interlocuteur avec qui il communique (conversation ouverte)
		 * @return Message
		 */
		static public Message sendText(byte[] data,Interlocuteurs emetteur, Interlocuteurs destinataire) {
			return new Message(Message.Type.DEFAULT,data,emetteur,destinataire);
			}
		/**
		 * @param bytes fichier en bytes
		 * @param emetteur l'utilisateur
		 * @param destinataire l' Interlocuteur avec qui il communique (conversation ouverte)
	     * @param name nom du fichier
		 * @return Message
		 */
		static public Message sendFile(byte[] data,Interlocuteurs emetteur, Interlocuteurs destinataire, String name) {
			return new Message(Message.Type.FILE,data,emetteur,destinataire,new Date(),name);
			}
		/**
		 * recrée un message depuis les données enregistrées dans la BD
		 * @param type si FILE data contiendra le nom du fichier
		 * @param data
		 * @param emetteur
		 * @param destinataire
		 * @param date
		 * @param name
		 * @return
		 */
		static public Message recreateMessageFromData(Type type,byte[] data,Interlocuteurs emetteur, Interlocuteurs destinataire,Date date) {
			if(type==Type.FILE)
			return new Message(type,"".getBytes(),emetteur,destinataire,date,new String(data));
			else
				return new Message(type,data,emetteur,destinataire,date,"");
			}
		/**
		 * @param emetteur l'utilisateur (s'il a déjà ou  attend déjà pour prendre ce pseudo)
		 * @param destinataire Interlocuteurs qui a demandé si son pseudo était déjà pris
		 * @return Message lui notifiant qu'il est déjà pris
		 */
		static public Message usernameAlreaydTaken(Interlocuteurs emetteur, Interlocuteurs destinataire) {
		return new Message(Message.Type.REPLYPSEUDO,null,emetteur,destinataire);
		}
		/**
		 * @param emetteur utilisateur (s'il est prêt à communiquer (pseudo choisi))
		 * @param destinataire Interlocuteurs qui a demandé qui était alive (en broadcast)
		 * @return Message
		 */
		static public Message userIsAlive(Interlocuteurs emetteur, Interlocuteurs destinataire) {
			return new Message(Message.Type.ALIVE,null,emetteur,destinataire);
			}
		
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message demandant aux autres utilisateurs de se notifier
		 */
		static public Message whoIsAliveBroadcast(Interlocuteurs emetteur) {
			return new Message(Message.Type.WHOISALIVE,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message notifiant la connexion
		 */
		static public Message userConnectedBroadcast(Interlocuteurs emetteur) {
			return new Message(Message.Type.CONNECTION,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur (avec son nouveau pseudo ! )
		 * @return Message notifiant le changement de pseudo
		 */
		static public Message switchPseudoBroadcast(Interlocuteurs emetteur) {
			return new Message(Message.Type.SWITCH,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message notifiant la déconnexion
		 */
		static public Message userDisconnectedBroadcast(Interlocuteurs emetteur) {
			return new Message(Message.Type.DECONNECTION,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur (avec le pseudo demandé déjà set )
		 * @return Message demandant une réponse si le pseudo est déjà pris
		 */
		static public Message askPseudoOkBroadcast(Interlocuteurs emetteur) {
			return new Message(Message.Type.ASKPSEUDO,emetteur);
			}
		/**
		 * @param emetteur utilisateur qui crée le groupe
		 * @param destinataires un groupe d'interlocuteur (pas de broadcast #confidentialité)
		 */
		static public Message createGroupe(Interlocuteurs emetteur,Interlocuteurs destinataires) {
			return new Message(Message.Type.GROUPCREATION,null,emetteur,destinataires);
			}
		/**
		 * 
		 * @return notify reception serveur ok
		 */
		static public Message okServeur() {
			return new Message(Message.Type.OKSERVEUR,null);
		}
		
		
	}
	public static byte[] serialize(Message mess) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(mess);
	    return out.toByteArray();
	}
	public static Message deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (Message) is.readObject();
	}
	public byte[] getData() {
		return data;
	}
	public Interlocuteurs getEmetteur() {
		return emetteur;
	}
	public String getNameFile() {
		return nameFile;
	}
	public Type getType() {
		return t;
	}
	public void setType(Message.Type typ) {
		this.t = typ;
	}

	public String getDateToString() { //pour stocker dans la bd...
		SimpleDateFormat toStr = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
		return toStr.format(this.date);
	}
	public static Date getStringToDate(String sdate) throws ParseException { //pour extraire de la bd...
		Date ladate = new SimpleDateFormat("yyyy-M-dd hh:mm:ss").parse(sdate);
		return ladate;
	}

	public String toHtml() {
		String beautiful="";
		if(t==Type.DEFAULT) {
			SimpleDateFormat heure = new SimpleDateFormat("hh:mm ");
			SimpleDateFormat jour = new SimpleDateFormat("EEEE d MMM ");
			try
		    {
				String url=new String(data);
		        new URL(url).toURI();
		        beautiful= "<a href=\""+url +"\">"+ url+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>"; 
		    } catch (Exception exception)
		    {
		    	beautiful= new String(data)+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>";
		    }	
			//si envoyé à un groupe 
			if(this.getDestinataire().getInterlocuteurs().size()>1)
				beautiful += "<div class='date'><b> de : "+emetteur.getPseudo()+"</b></div>";
		}
		else if(t==Type.FILE) {
			SimpleDateFormat heure = new SimpleDateFormat("hh:mm ");
			SimpleDateFormat jour = new SimpleDateFormat("EEEE d MMM ");
			System.out.print("<a href='"+nameFile +"'>"+ new File(nameFile).getName()+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>");
			beautiful= "<a href=\""+nameFile +"\">"+ new File(nameFile).getName()+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>";
			//si envoyé à un groupe 
			if(this.getDestinataire().getInterlocuteurs().size()>1)
				beautiful += "<div class='date'><b> de : "+emetteur.getPseudo()+"</b></div>";
		}
	
			return beautiful;
	}
	public Interlocuteurs getDestinataire() {
		return destinataire;
	}
	public void setNameFile(String name) {
		nameFile=name;
	}
}
