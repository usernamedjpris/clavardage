package clavardeur;
import java.io.IOException;
import java.net.*;

public class BroadcastClient {
    private static DatagramSocket socket = null;
  
    public static void broadcast(String broadcastMessage) throws IOException, SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
 
        byte[] buffer = broadcastMessage.getBytes();
 
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 4445);
        socket.send(packet);
        socket.close();
    }
}
