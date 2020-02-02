import java.net.*;

import com.clava.serializable.Message;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;

//public class ServeurSocketThread extends Observable implements Runnable {
public class ServeurSocketThread implements  Runnable {
    Socket s;
    private PropertyChangeSupport support;
    /**
     * Constructeur ServeurSocketThread
     * <p>[Design Pattern Observers]</p>
     * @param soc
     */
    public ServeurSocketThread(Socket soc) {
        this.s = soc;
        support = new PropertyChangeSupport(this);
    }
    /**
     * Ajoute un Listener à notifier (ServeurTCP)
     * @param pcl
     * @see ServeurTCP
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
    /**
     * Remonte le message reçu et déserializé au ServeurTCP
     */
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
            	support.firePropertyChange("message","", Message.deserialize(data));
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