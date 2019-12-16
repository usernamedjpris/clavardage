import java.net.*;
import java.util.Observable;
import java.util.Observer;
import java.io.*;


public class ServeurTCP extends Observable implements Observer, Runnable {
	ServerSocket ssoc = null;
	boolean on=true;
	public ServeurTCP() {}
    
	public void update(Observable o, Object arg) {
		System.out.print("\n I am notified ! (1st)");
		this.setChanged();
		notifyObservers(arg);
	}
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
		try {
			ssoc = new ServerSocket(1028);
		} catch (IOException e) {
			e.printStackTrace();
		}        
        while(on){//Waiting connections
            Socket soc = null;
			try {
				soc = ssoc.accept();
				System.out.print(" \n Accepted connexion ! ");
				ServeurSocketThread st = new ServeurSocketThread(soc);
	            st.addObserver(this); 
	            Thread th = new Thread(st);
	            th.start();
			} catch (IOException e) {
				if(on)
				e.printStackTrace();
			}
        }
	}
}