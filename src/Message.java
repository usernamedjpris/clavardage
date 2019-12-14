import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
	public enum Type {DECONNECTION, SWITCH, CONNECTION, WHOISALIVE, ALIVE, DEFAULT}
	byte[] data;
	Personne emetteur;
	Personne destinataire;
	Date date;
	Type t;
	String newPseudo;
	/**
	 * @param data
	 * @param emetteur
	 * @param destinataire
	 * @param date
	 * @param type
	 * @param newPseudo
	 */
	//DATE à générer lors de la création du message => pas en parametre du constructeur #indépendance
	public Message(byte[] data, Personne emetteur, Personne destinataire, Date date) {
		this.data = data;
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=Type.DEFAULT;
		this.newPseudo = emetteur.getPseudo();
	}
	public Message(String data, Personne emetteur, Personne destinataire, Date date) {
		this(data.getBytes(), emetteur, destinataire, date);
	}
	public Message(Type typ, Personne emetteur, Personne destinataire, Date date) {
		this.data = "".getBytes();
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=typ;	
		this.newPseudo = emetteur.getPseudo();
	}
	public Message(Personne emetteur, Personne destinataire, Date date, String newPseudo) {
		this.data = "".getBytes();
		this.emetteur = emetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=Type.SWITCH;
		this.newPseudo = newPseudo;
	}
	//broadcast
	public Message(Type cat, Personne personne) {
		emetteur=personne;
		t=cat;
		try {
			destinataire=new Personne(InetAddress.getByName("255.255.255.255"), "all");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		date=new Date();
	}
	public Message(Type cat, Personne personne, String newPseudo) {
		this(cat,personne);
		this.newPseudo=newPseudo;
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
	public Personne getEmetteur() {
		return emetteur;
	}
	public String getNewPseudo() {
		return newPseudo;
	}
	public Type getType() {
		return t;
	}
	public String toHtml() {
		// TODO Auto-generated method stub
		if(t==Type.DEFAULT) {
			return "<p>"+new String(data)+"</p>"; //image ??
		}
		else
			return "";
	}

}
