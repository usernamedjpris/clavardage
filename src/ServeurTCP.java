
import java.net.*;
import java.util.Observable;
import java.util.Observer;
import java.io.*;


public class ServeurTCP extends Observable implements Observer, Runnable {
	ServerSocket ssoc = null;
	boolean on=true;
	public ServeurTCP() {}
    
	public void update(Observable o, Object arg) {
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
				ServeurSocketThread st = new ServeurSocketThread(soc);
	            Thread th = new Thread(st);
	            th.start();
	            st.addObserver(this); 
			} catch (IOException e) {
				if(on)
				e.printStackTrace();
			}
        }
	}
}