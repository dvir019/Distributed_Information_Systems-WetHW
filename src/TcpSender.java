import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TcpSender extends Thread {
    private int round;
    private Neighbor neighborToSendTo;
    private int[] du;

    public TcpSender(int round, Neighbor neighborToSendTo) {
        this.round = round;
        this.neighborToSendTo = neighborToSendTo;
    }

    @Override
    public void run() {
        String ip = neighborToSendTo.getIp();
        int port = neighborToSendTo.getTcpPort();

        try {
            Socket socket = new Socket(ip, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(round);
            outputStream.flush();
            String message = inputStream.readUTF();
            System.out.println("Got tcp message: " + message);
            if (message.contains("-")) {
                System.out.println("Hey!");
            }
            du = Arrays.stream(message.substring(1, message.length()-1).split(", ")).mapToInt(Integer::parseInt).toArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getDu() {
        return du;
    }

    public int getNeighborName() {
        return neighborToSendTo.getName();
    }
}
