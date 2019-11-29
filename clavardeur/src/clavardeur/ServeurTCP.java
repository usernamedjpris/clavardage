package clavardeur;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
public class ServeurTCP{
	HashMap <Personne, Byte[]> bufferReception;
	Reseau network;
	public ServeurTCP(Reseau network) {
		this.bufferReception = new HashMap <Personne, Byte[]>;
		this.network = network;
	}
	public void getData() throws IOException{
        //creation objet ServerSocket
        ServerSocket ServeurTCP = new ServerSocket(1025);
        //Waiting connexion
        Socket s = ServerTCP.accept();        
        //Set up INput streams
        InputStream is = s.getInputStream();
        //Recevoir les datas
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        //System.out.println(in.readLine()+"\n");
        this.network.notify();
        //Clore la connexion
        s.close();
        

    }
	/*quand reçoit données fait un this.network.notify()*/
}
