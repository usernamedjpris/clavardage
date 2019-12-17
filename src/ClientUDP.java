
import java.io.IOException;
import java.net.*;

//https://www.baeldung.com/java-broadcast-multicast
public class ClientUDP {

    public void broadcast(Message message) throws IOException, SocketException {
    	DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
 
        byte[] buffer = Message.serialize(message);
        InetAddress a=InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,a, 1516);
        socket.send(packet);
        socket.close();
    }
    
    public void send(Message message) throws IOException, SocketException {
    	DatagramSocket socket = new DatagramSocket();
        try {
        	byte[] buffer = Message.serialize(message);
			System.out.print("Size serialized :"+buffer.length);
			/*int len = buffer.length;
			System.out.print("Message get : "+new String(buffer)+" len :"+len);
			//peut-etre il existe un moyen de rajouter la longueur devant le tableau de byte plus joliement
			byte[] buffer2 = new byte[len+1];
			buffer2[0] = (byte) len;
			for (int i = 1 ; i < buffer2.length ; i++) {
				buffer2[i] = buffer[i-1];
			}*/
        	
			InetAddress a=message.destinataire.getAdresse();
	        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, a, 1516);
	      /*  packet.setData(buf, offset, length);
	        packet.setAddress(iaddr);
	        packet.setPort(iport);*/
	        socket.send(packet);
	        socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
}
