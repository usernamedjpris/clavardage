package clavardeur;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class Message {
	public enum Type {DECONNECTION, SWITCH, CONNECTION, ALIVE, DEFAULT};
	byte[] data;
	Personne emmetteur;
	Personne destinataire;
	Date date;
	Type t;
	String newPseudo;
	/**
	 * @param data
	 * @param emmetteur
	 * @param destinataire
	 * @param date
	 * @param type
	 * @param newPseudo
	 */
	public Message(byte[] data, Personne emmetteur, Personne destinataire, Date date) {
		this.data = data;
		this.emmetteur = emmetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=Type.DEFAULT;
		this.newPseudo = emmetteur.getPseudo();
	}
	public Message(String data, Personne emmetteur, Personne destinataire, Date date) {
		this(data.getBytes(), emmetteur, destinataire, date);
	}
	public Message(Type typ, Personne emmetteur, Personne destinataire, Date date) {
		this.data = "".getBytes();
		this.emmetteur = emmetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=typ;	
		this.newPseudo = emmetteur.getPseudo();
	}
	public Message(Personne emmetteur, Personne destinataire, Date date, String newPseudo) {
		this.data = "".getBytes();
		this.emmetteur = emmetteur;
		this.destinataire = destinataire;
		this.date = date;
		this.t=Type.SWITCH;
		this.newPseudo = newPseudo;
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

}
