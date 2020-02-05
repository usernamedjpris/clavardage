package com.clava.model.reseau;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import com.clava.serializable.Message;
//https://www.baeldung.com/java-broadcast-multicast
/**
 * ClientUDP permet l'envoi de messages avec le protocole UDP 
 */
public class ClientUDP {
	int portUDP;
	 List<InetAddress> broadcastList;
	public ClientUDP(int port) {
		portUDP=port;
		try {
			broadcastList=listAllBroadcastAddresses();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Permet d'envoyer un message en broadcast (reseau local)
	 * <p> utile pour les messages du type DECONNECTION, SWITCH, CONNECTION, WHOISALIVE, ASKPSEUDO, GROUPCREATION</p>
	 * @param message
	 * @throws IOException
	 * @throws SocketException
	 */
    public void broadcast(Message message) throws IOException, SocketException {
    	System.out.print("\n"+message.getEmetteur().getPseudo()+" envoi le message "+message.getType().toString()+" en broadcast "+
    "sur le port :"+portUDP);
		
    	DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
 
        byte[] buffer = Message.serialize(message);
        for(InetAddress a:broadcastList) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,a, portUDP);
        socket.send(packet);
        }
        socket.close();
    }
    /**
     * Liste toutes les adresses de broadcast disponibles 
     * @return
     * @throws SocketException
     */
    List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces 
          = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
     
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
     
            networkInterface.getInterfaceAddresses().stream() 
              .map(a -> a.getBroadcast())
              .filter(Objects::nonNull)
              .forEach(broadcastList::add);
        }
        return broadcastList;
    }
    /**
     * R UDP not for group
     * utile pour ALIVE, REPLYPSEUDO
     * @param message a envoyer en udp
     * @throws IOException
     * @throws SocketException
     */
    public void send(Message message) throws IOException, SocketException {
    	DatagramSocket socket = new DatagramSocket();
        try {
        	byte[] buffer = Message.serialize(message);
			/*int len = buffer.length;
			System.out.print("Message get : "+new String(buffer)+" len :"+len);
			//peut-etre il existe un moyen de rajouter la longueur devant le tableau de byte plus joliement
			byte[] buffer2 = new byte[len+1];
			buffer2[0] = (byte) len;
			for (int i = 1 ; i < buffer2.length ; i++) {
				buffer2[i] = buffer[i-1];
			}*/
			InetAddress a=message.getDestinataire().getAddressAndPorts().get(0).getKey();
	        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, a, portUDP);
	   
	        socket.send(packet);
	        socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
}
