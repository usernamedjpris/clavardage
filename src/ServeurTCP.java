import java.net.*;
import java.util.Observable;
import java.util.Observer;
import java.io.*;


@SuppressWarnings("deprecation")
public class ServeurTCP extends Observable implements Observer, Runnable {
	ServerSocket ssoc = null;
	boolean on=true;
	private int port;
	public ServeurTCP(int port) {this.port=port;}
    
	public void update(Observable o, Object arg) {
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