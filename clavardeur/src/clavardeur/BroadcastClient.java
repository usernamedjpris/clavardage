package clavardeur;
import java.io.IOException;
import java.net.*;

public class BroadcastClient {
    private DatagramSocket socket = null;
  
    public void broadcast(Message message) throws IOException, SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
 
        byte[] buffer = message.getData();
 
        DatagramPacket packet 
          = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 4445);
        socket.send(packet);
        socket.close();
    }
}
