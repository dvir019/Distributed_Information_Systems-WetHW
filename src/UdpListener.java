import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpListener extends Thread {
    private int udpPort;
    private DatagramSocket socket;
    private int num;

    public UdpListener(int udpPort, int num) {
        this.udpPort = udpPort;
        this.num = num;
        try {
            socket = new DatagramSocket(udpPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[4096];
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Router " + num + " Got message: " + received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
