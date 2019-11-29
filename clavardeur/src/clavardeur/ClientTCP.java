package clavardeur;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
public class ClientTCP {
	HashMap <Personne, Byte[]> bufferEnvoi;
	
    public void sendMessage (Message mess, Personne pers) throws IOException{
        //Initier la connexion
        Socket s = new Socket ("127.0.0.1",1025); //127.0.0.1 == localhost
        //Set up OUTput streams
        OutputStream os = s.getOutputStream();
        //Envoyer les datas       
        Date date = new Date(); //exo3
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        out.write(date.toString()+"\n");
        System.out.println(date.toString());
        out.flush();
        //Clore la connexion
        s.close();	    
	}
}
