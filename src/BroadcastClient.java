
import java.io.IOException;
import java.net.*;

//https://www.baeldung.com/java-broadcast-multicast
public class BroadcastClient {
    private DatagramSocket socket = null;
  
    public void broadcast(Message message) throws IOException, SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
 
        byte[] buffer = Message.serialize(message);
        InetAddress a=InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,a, 4446);
        socket.send(packet);
        socket.close();
    }
}
