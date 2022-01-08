import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TcpHandler extends Thread{
    private Socket socket;
    private Router router;

    public TcpHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            int round = inputStream.readInt();

            System.out.println("Router " + router.getRouterName() + " got request to send update " + round + "(It's round " + router.updateNumber.get() + " for the router)");
            while (round > router.updateNumber.get()) {
                System.out.println("Waiting!!!!!!!!!!!!!!!!!!!!");
            }
            synchronized (router.routingTableLock) {  // Add synchronized (router.routingTableLock)
                RoutingTable routingTable = router.getRoutingTable();
                int[] distancesAfter = routingTable.getDistancesAfter();
                String distancesAfterAsString = Arrays.toString(distancesAfter);
                outputStream.writeUTF(distancesAfterAsString);
                outputStream.flush();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
