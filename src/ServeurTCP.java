import java.net.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;

public class ServeurTCP implements PropertyChangeListener, Runnable{
	ServerSocket ssoc = null;
	boolean on=true;
	private int port;
	private PropertyChangeSupport support;
	/**
	 * Constructeur ServeurTCP
	 * <p>[Design Pattern Observers]</p>
	 * @param port
	 */
	public ServeurTCP(int port) {
		this.port=port;
		support = new PropertyChangeSupport(this);
	}
    /**
     * Ajoute un Listener à notifier (Reseau)
     * @param pcl
     * @see Reseau
     */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
	/**
	 * Permet de fermer en bonne et due forme le ServerSocket TCP et de libérer ainsi le port d'écoute pour la prochaine fois
	 */
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
	/**
	 * Le serveurTCP se met en état accept et à chaque demande de connexion lance un nouveau threa ServeurSocketThrad dédié
	 */
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
				ServeurSocketThread st = new ServeurSocketThread(soc);
	            st.addPropertyChangeListener(this); 
	            Thread th = new Thread(st);
	            th.start();
			} catch (IOException e) {
				if(on)
				e.printStackTrace();
			}
        }
	}
	/**
	 * Reçoit message [Design Pattern Observers] de ServeurSocketThread et le transmet directement par le même moyen a la classe Reseau
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange("message", evt.getOldValue(), evt.getNewValue());
	}
}