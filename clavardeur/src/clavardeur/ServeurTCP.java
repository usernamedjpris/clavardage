package clavardeur;
import java.net.*;
import java.io.*;


public class ServeurTCP{
    public ServeurTCP() {
    	
    }
	public void launch() throws IOException {
        @SuppressWarnings("resource")
		ServerSocket ssoc = new ServerSocket(1025);        
        while(true){//Waiting connections (while true to be improved ?)
            Socket soc = ssoc.accept();
            ServeurSocketThread st = new ServeurSocketThread(soc);
            Thread th = new Thread(st);
            th.start();
        }
    }
}