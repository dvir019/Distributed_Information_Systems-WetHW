import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpListener extends Thread {
    private ServerSocket serverSocket;

    public TcpListener(int tcpPort) {
        try {
            serverSocket = new ServerSocket(tcpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {  // TODO: Add some flag
            try {
                Socket newSocket = serverSocket.accept();
                TcpHandler handler = new TcpHandler(newSocket);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
