import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
            while (round > router.updateNumber.get()) {

            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
