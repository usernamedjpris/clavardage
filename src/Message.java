import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
public class Message implements Serializable {

	private static final long serialVersionUID = -8338507989483169683L;
	public enum Type {DECONNECTION, SWITCH, CONNECTION, WHOISALIVE, ALIVE,ASKPSEUDO,REPLYPSEUDO, FILE, DEFAULT}
	private byte[] data;
	private Personne emetteur;
	private Personne destinataire;
	private Date date;
	private Type t;
	private String nameFile;
	
	/**
	 * Message à une personne TCP ou UDP (si réponse broadcast)
	 * @param cat type de message entre ALIVE, REPLYPSEUDO, DEFAULT
	 * @param data texte si DEFAULT, null sinon
	 * @param emetteur
	 * @param destinataire
	 */
	private Message(Type cat,byte[] data, Personne emetteur, Personne destinataire) {
		this.data = data;
		this.emetteur = emetteur;
		this.destinataire=(destinataire);
		this.date = new Date();
		this.t=cat;
		this.nameFile = "";
	}
	
	/**
	 * Broadcast Message UDP
	 * @param cat type entre SWITCH, CONNEXION, DECONNEXION, WHOISALIVE, ASKPSEUDO
	 * @param personne c-a-d l'emetteur
	 * <br> rq pour SWITCH le nouveau pseudo est dans l'emetteur( check les id et maj en reception)
	 * <br> idem pour ASKPSEUDO
	 */
	private Message(Type cat, Personne personne) {
		destinataire=null;
		emetteur=personne;
		t=cat;
		date=new Date();
	}
	/**
	 * All inclusive builder, for FILE or BD 
	 * @param bytes
	 * @param typ
	 * @param emet
	 * @param interlocuteur
	 * @param date2 date d'envoi
	 * @param name nom du fichier
	 */
	private Message(Type typ,byte[] bytes, Personne emet, Personne interlocuteur, Date date2,String name) {
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
		 * @param destinataire la personne avec qui il communique (conversation ouverte)
		 * @return Message
		 */
		static public Message sendText(byte[] data,Personne emetteur, Personne destinataire) {
			return new Message(Message.Type.DEFAULT,data,emetteur,destinataire);
			}
		/**
		 * @param bytes fichier en bytes
		 * @param emetteur l'utilisateur
		 * @param destinataire la personne avec qui il communique (conversation ouverte)
	     * @param name nom du fichier
		 * @return Message
		 */
		static public Message sendFile(byte[] data,Personne emetteur, Personne destinataire, String name) {
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
		static public Message recreateMessageFromData(Type type,byte[] data,Personne emetteur, Personne destinataire,Date date) {
			if(type==Type.FILE)
			return new Message(type,"".getBytes(),emetteur,destinataire,date,new String(data));
			else
				return new Message(type,data,emetteur,destinataire,date,"");
			}
		/**
		 * @param emetteur l'utilisateur (s'il a déjà ou  attend déjà pour prendre ce pseudo)
		 * @param destinataire personne qui a demandé si son pseudo était déjà pris
		 * @return Message lui notifiant qu'il est déjà pris
		 */
		static public Message usernameAlreaydTaken(Personne emetteur, Personne destinataire) {
		return new Message(Message.Type.REPLYPSEUDO,null,emetteur,destinataire);
		}
		/**
		 * @param emetteur utilisateur (s'il est prêt à communiquer (pseudo choisi))
		 * @param destinataire personne qui a demandé qui était alive (en broadcast)
		 * @return Message
		 */
		static public Message userIsAlive(Personne emetteur, Personne destinataire) {
			return new Message(Message.Type.ALIVE,null,emetteur,destinataire);
			}
		
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message demandant aux autres utilisateurs de se notifier
		 */
		static public Message whoIsAliveBroadcast(Personne emetteur) {
			return new Message(Message.Type.WHOISALIVE,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message notifiant la connexion
		 */
		static public Message userConnectedBroadcast(Personne emetteur) {
			return new Message(Message.Type.CONNECTION,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur (avec son nouveau pseudo ! )
		 * @return Message notifiant le changement de pseudo
		 */
		static public Message switchPseudoBroadcast(Personne emetteur) {
			return new Message(Message.Type.SWITCH,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur
		 * @return Message notifiant la déconnexion
		 */
		static public Message userDisconnectedBroadcast(Personne emetteur) {
			return new Message(Message.Type.DECONNECTION,emetteur);
			}
		/**
		 *Attention crée un message uniquement destiné à du broadcast (pas de destinataire)
		 * @param emetteur utilisateur (avec le pseudo demandé déjà set )
		 * @return Message demandant une réponse si le pseudo est déjà pris
		 */
		static public Message askPseudoOkBroadcast(Personne emetteur) {
			return new Message(Message.Type.ASKPSEUDO,emetteur);
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
	public Personne getEmetteur() {
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
		if(t==Type.DEFAULT) {
			SimpleDateFormat heure = new SimpleDateFormat("hh:mm ");
			SimpleDateFormat jour = new SimpleDateFormat("EEEE d MMM ");
			try
		    {
				String url=new String(data);
		        new URL(url).toURI();
		        return "<a href=\""+url +"\">"+ url+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>"; 
		    } catch (Exception exception)
		    {
		    	return new String(data)+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>";
		    }		 
		}
		else if(t==Type.FILE) {
			SimpleDateFormat heure = new SimpleDateFormat("hh:mm ");
			SimpleDateFormat jour = new SimpleDateFormat("EEEE d MMM ");
			System.out.print("<a href='"+nameFile +"'>"+ new File(nameFile).getName()+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>");
			return  "<a href=\""+nameFile +"\">"+ new File(nameFile).getName()+"</a>"+"<div class='date'><b>"+heure.format(date)+"</b>"+jour.format(date)+"</div>";
		}
		else
			return "";
	}
	public Personne getDestinataire() {
		return destinataire;
	}
	public void setNameFile(String name) {
		nameFile=name;
	}
}
