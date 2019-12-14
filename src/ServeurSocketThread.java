
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Observable;

public class ServeurSocketThread extends Observable implements Runnable {
    Socket s;
    public ServeurSocketThread(Socket soc) {
        super();
        this.s = soc;
    }

	@Override
    public void run() {
        try{      
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
            //bufferReception.add(Message.deserialize(data)); //a quoi sert il ce buffer ?
            try {
				this.notifyObservers(Message.deserialize(data));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            //out.flush();
            //Clore la connexion
            s.close();
        }
        catch (IOException e){
            System.out.println("I03xception :)"); 
        }
    }
}