import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpHandler extends Thread{
    private Socket socket;

    public TcpHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//            byte[] result = new byte[];
//            inputStream.read;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
