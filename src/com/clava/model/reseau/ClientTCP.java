package com.clava.model.reseau;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.AbstractMap.SimpleEntry;

import com.clava.serializable.Message;
/**
 * ClientHTCP permet l'envoi de messages de protocole TCP
 */
public class ClientTCP {
	/**
	 * Permet d'envoyer un message m à tous les interlocuteurs destinataires (si groupe) ou à la personne destinataire sinon 
	 * @param m
	 * @throws IOException
	 */
    public void sendMessage (Message m) throws IOException{ //String data, Personne dest, Personne emmet
    	for(SimpleEntry<InetAddress,Integer> a:m.getDestinataire().getAddressAndPorts()) {
        //Initier la connexion
		/*    the NAT uses "endpoint independent mapping": two successive TCP connections coming from the same internal endpoint are mapped
		 *  to the same public endpoint.With this solution, the peers will first connect to a third party server that will save their port 
		 *  mapping value and give to both peers the port mapping value of the other peer. In a second step, both peers will reuse the same 
		 *  local endpoint to perform a TCP simultaneous open with each other. This unfortunately requires the use of the SO_REUSEADDR on the
		 *   TCP sockets, and such use violates the TCP standard and can lead to data corruption. It should only be used if the application 
		 *   can protect itself against such data corruption. 
		*/
       Socket s = new Socket ();//(a.getKey(),a.getValue()); //127.0.0.1 == localhost
        SocketAddress sockaddr = new InetSocketAddress(a.getKey(),a.getValue());
       // s.setReuseAddress(true);
       // s.bind(new InetSocketAddress(m.getEmetteur().getAddressAndPorts().get(0).getKey(),3526));
       	s.connect(sockaddr, 4000);//timeout 4s 
        //Set up OUTput streams
        OutputStream os = s.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        //Envoyer les datas       
        try {
			byte[] byteMessage = Message.serialize(m);
			int len = byteMessage.length;
			dos.writeInt(len);
			if (len > 0) {
			    dos.write(byteMessage, 0, len);
			    dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        //Clore la connexion
        dos.close();
        os.close();
        s.close();	  
    	}
	}
}
