package clavardeur;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Reseau extends Observable {
	ArrayList <Message> bufferReception;
	//ServeurTCP reception;
	ClientTCP envoi;
	BroadcastClient clientUDP;
	static Reseau theNetwork;
	/**
	 * @param reception
	 * @param envoi
	 * @param clientUDP
	 */
	private Reseau() {
		//this.reception = new ServeurTCP(this.getReseau());
		this.envoi = new ClientTCP();
		this.clientUDP = new BroadcastClient();
		this.bufferReception = new ArrayList <Message>();
	}

	public Reseau getReseau() {
		if (theNetwork == null) {
			theNetwork = new Reseau();
		} 
		return theNetwork;
	}
	
	public void sendData(Message message, Personne pers) {
		envoi.sendData(message, pers);
	}
	
	public void sendDataBroadcast(String strMessage) throws SocketException, IOException {
		clientUDP.broadcast(strMessage);
	}
	
	public void getData() throws IOException, ClassNotFoundException{
        //creation objet ServerSocket
        ServerSocket ServeurTCP = new ServerSocket(1025);
        //Waiting connexion
        Socket s = ServeurTCP.accept();        
        //Set up INput streams
        InputStream is = s.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        //Recevoir les datas
        ////// BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        
        bufferReception.add(Message.deserialize(data)); //a quoi sert il ce buffer ?
        this.notifyObservers(Message.deserialize(data));
        //Clore la connexion
        s.close();
    }
}
