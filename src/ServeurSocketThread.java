import java.net.*;
import java.io.*;
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
           
            InputStream is = s.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            }
            
            try {
            	this.setChanged();
				this.notifyObservers(Message.deserialize(data));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            //Clore la connexion
            s.close();
        }
        catch (IOException e){
            System.out.println("I03xception :)");
        }
    }
}