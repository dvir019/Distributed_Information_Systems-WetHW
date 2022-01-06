import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UdpListener extends Thread {
    private Router router;
    private int udpPort;
    private DatagramSocket socket;
    private int routerName;

    public UdpListener(Router router) {
        this.router = router;
        this.udpPort = router.getUdpPort();
        this.routerName = router.getRouterName();
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
//                packet = new DatagramPacket(buf, buf.length, address, port);
                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Router " + routerName + " Got message with len= "+ message.length() + ": " +  message);
                UdpHandler handler = new UdpHandler(address, port, message, router, socket);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
