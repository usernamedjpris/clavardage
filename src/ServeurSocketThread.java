import java.net.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;

//public class ServeurSocketThread extends Observable implements Runnable {
public class ServeurSocketThread implements  Runnable {
    Socket s;
    private PropertyChangeSupport support;
    public ServeurSocketThread(Socket soc) {
        this.s = soc;
        support = new PropertyChangeSupport(this);
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
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