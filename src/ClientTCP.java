
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import com.clava.serializable.Message;
import com.clava.serializable.Personne;
public class ClientTCP {
	HashMap<Integer,Socket> map;
	public ClientTCP() {
		map=new HashMap<Integer,Socket>();
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
	         map.forEach((Integer a,Socket s)-> {
				try {
					System.out.print(" CLose socket");
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			    }});
	}
	
    public void sendMessage (Message m) throws IOException{ //String data, Personne dest, Personne emmet
    	for(SimpleEntry<InetAddress,Integer> a:m.getDestinataire().getAddressAndPorts()) {
        //Initier la connexion
/*    the NAT uses "endpoint independent mapping": two successive TCP connections coming from the same internal endpoint are mapped
 *  to the same public endpoint.With this solution, the peers will first connect to a third party server that will save their port 
 *  mapping value and give to both peers the port mapping value of the other peer. In a second step, both peers will reuse the same 
 *  local endpoint to perform a TCP simultaneous open with each other. This unfortunately requires the use of the SO_REUSEADDR on the
 *   TCP sockets, and such use violates the TCP standard and can lead to data corruption. It should only be used if the application 
 *   can protect itself against such data corruption. 
*/		Socket s=map.get(m.getEmetteur().getId());
    	if(s== null) {
        s = new Socket (a.getKey(),a.getValue()); //127.0.0.1 == localhost
        s.setKeepAlive(true);
        map.put(m.getEmetteur().getId(), s);
    	}
        /*SocketAddress sockaddr = new InetSocketAddress(a.getKey(),a.getValue());
        s.setReuseAddress(true);
        s.bind(new InetSocketAddress(m.getEmetteur().getAddressAndPorts().get(0).getKey(),3526));
       	s.connect(sockaddr, 2000);*/
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
        os.flush();
        //dos.close();
       /* os.close();
       // s.close();*/	  
    	}
	}

}
