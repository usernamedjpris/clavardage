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
	public Message(Type cat,byte[] data, Personne emetteur, Personne destinataire) {
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
	public Message(Type cat, Personne personne) {
		destinataire=null;
		emetteur=personne;
		t=cat;
		date=new Date();
	}
	/** Envoi de fichiers
	 * @param bytes  fichier lu en bytes
	 * @param emet personne qui emet
	 * @param interlocuteur personne à qui envoyer le message
	 * @param name nom du fichier
	 */
	public Message(byte[] bytes, Personne emet, Personne interlocuteur, String name) {
		this(bytes,emet,interlocuteur,Type.FILE,new Date(),name);
	}
	/**
	 * Pour recréer les messages déjà envoyés (BD)
	 * @param bytes
	 * @param emet
	 * @param interlocuteur
	 * @param typ
	 * @param date2 date d'envoi
	 * @param name nom du fichier
	 */
	public Message(byte[] bytes, Personne emet, Personne interlocuteur, Type typ, Date date2,String name) {
		data=bytes;
		emetteur=emet;
		destinataire=interlocuteur;
		date=date2;
		t=typ;
		nameFile=name;
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
