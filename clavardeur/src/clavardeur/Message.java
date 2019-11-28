package clavardeur;

import java.sql.Date;

public class Message {
	byte[] data;
	Personne emmetteur;
	Date date;
	/**
	 * @param data
	 * @param emmetteur
	 * @param date
	 */
	public Message(byte[] data, Personne emmetteur, Date date) {
		super();
		this.data = data;
		this.emmetteur = emmetteur;
		this.date = date;
	}
	public Message(String data, Personne emmetteur, Date date) {
		this(data.getBytes(),emmetteur,date);
	}

}
