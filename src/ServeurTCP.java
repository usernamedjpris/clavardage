import java.net.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;


//public class ServeurTCP extends Observable implements Observer, Runnable {
public class ServeurTCP implements PropertyChangeListener, Runnable{
	ServerSocket ssoc = null;
	boolean on=true;
	private int port;
	private PropertyChangeSupport support;
	HashMap<Integer,ServeurSocketThread> map;
	int nb=0;
	public ServeurTCP(int port) {
		this.port=port;
		support = new PropertyChangeSupport(this);
		map=new HashMap<Integer,ServeurSocketThread>();
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
	         map.forEach((Integer a,ServeurSocketThread s)-> {
				System.out.print(" CLose socket");
				s.close();
			});
			    }});
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
	/*public void update(Observable o, Object arg) {
		this.setChanged();
		notifyObservers(arg);
	}*/
	public void closeServeur() { 
        try {
        	if(ssoc != null) {
        	on=false;
			ssoc.close();
			System.out.print("Collected socket TCP ! (closed)");
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
    public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
         closeServeur();
		    }});
		
		try {
			ssoc = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}        
        while(on){//Waiting connections
            Socket soc = null;
			try {
				soc = ssoc.accept();
				nb++;
				ServeurSocketThread st = new ServeurSocketThread(soc);
				map.put(nb,st);
	            st.addPropertyChangeListener(this); 
	            Thread th = new Thread(st);
	            th.start();
			} catch (IOException e) {
				if(on)
				e.printStackTrace();
			}
        }
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange("message", evt.getOldValue(), evt.getNewValue());
	}
}