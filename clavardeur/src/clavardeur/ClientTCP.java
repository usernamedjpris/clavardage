package clavardeur;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
public class ClientTCP {
	HashMap <Personne, Byte[]> bufferEnvoi;
	
    public void sendMessage (String mess, Personne pers) throws IOException{
        //Initier la connexion
        Socket s = new Socket ("127.0.0.1",1025); //127.0.0.1 == localhost
        //Set up OUTput streams
        OutputStream os = s.getOutputStream();
        //Envoyer les datas       
        Date date = new Date(); //exo3
        PrintWriter out = new PrintWriter(os,true);
        out.write(date.toString()+"—"+mess+"—"+pers.getPseudo()+"\n");
        System.out.println("-> envoi : "+date.toString()+"|"+mess+"|"+pers.toString());
        out.flush();
        //Clore la connexion
        s.close();	    
	}
}
