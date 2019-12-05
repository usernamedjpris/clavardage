package clavardeur;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;

public class Message {
	byte[] data;
	Personne emmetteur;
	Date date;
	public enum Type {DECONNECTION, SWITCH, CONNECTION, DEFAULT};
	Type t;
	/**
	 * @param data
	 * @param emmetteur
	 * @param date
	 * @param type
	 */
	public Message(byte[] data, Personne emmetteur, Date date) {
		super();
		this.data = data;
		this.emmetteur = emmetteur;
		this.date = date;
		this.t=Type.DEFAULT;
	}
	public Message(String data, Personne emmetteur, Date date) {
		this(data.getBytes(),emmetteur,date);
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
