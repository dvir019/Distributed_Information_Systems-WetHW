import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UdpHandler extends Thread {
    private InetAddress senderAddress;
    private int senderPort;
    private String senderMessage;
    private Router router;
    private DatagramSocket socket;

    public UdpHandler(InetAddress senderAddress, int senderPort, String senderMessage, Router router, DatagramSocket socket) {
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.senderMessage = senderMessage;
        this.router = router;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Started UdpHandler. message = " + senderMessage + ", " + UdpMessages.PRINT_MESSAGE);
        if (senderMessage.equals(UdpMessages.PRINT_MESSAGE)) {
            printRoutingTable();
        } else if (senderMessage.equals(UdpMessages.UPDATE_MESSAGE)) {
            updateRoutingTable();
        } else if (senderMessage.equals(UdpMessages.SHUT_DOWN_MESSAGE)) {
            shutDown();  // TODO: Maybe not here, but in the listener
        } else {
            forward();
        }
    }

    private void printRoutingTable() {
        System.out.println("In printRoutingTable");
        RoutingTable routingTable = router.getRoutingTable();
        String tableFileName = router.getTableFile();
        String routingTableString;

        synchronized (router.routingTableLock) {
            routingTableString = routingTable.toString();
        }

        synchronized (router.tableFileLock) {
            appendToFile(tableFileName, routingTableString);
        }

        sendFinish();
    }

    private void updateRoutingTable() {
        int round = router.updateNumber.get();
        int routerName = router.getRouterName();
        RoutingTable routingTable = router.getRoutingTable();

//        routingTable.setDistancesAsDistancesAfter();

        List<TcpSender> senders = new ArrayList<>();

        for (Neighbor neighbor : router.getNeighborsMap().values()) {
            TcpSender sender = new TcpSender(round, neighbor);
            senders.add(sender);
            sender.start();
        }

        for (TcpSender sender : senders) {
            try {
                sender.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<Integer, int[]> dus = new HashMap<>();

        for (TcpSender sender : senders) {
            dus.put(sender.getNeighborName(), sender.getDu());
        }

        for (int x = 1; x <= router.getNumOfRouters(); x++) {
            if (x != routerName) {
                int minNeighbor = -1;
                int minDistanceToX = Integer.MAX_VALUE;
                for (Neighbor neighbor : router.getNeighborsMap().values()) {
                    int neighborName = neighbor.getName();
                    int edgeWeight = neighbor.getEdgeWeight();
                    int distance = dus.get(neighborName)[x - 1];
                    int distanceToX = edgeWeight + distance;
                    if (distanceToX <= 0) {
                        System.out.println("Fuck!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                    if (distanceToX < minDistanceToX) {
                        minDistanceToX = distanceToX;
                        minNeighbor = neighborName;
                    }
                }
                synchronized (router.routingTableLock) {  // Add synchronized (router.routingTableLock)
                    routingTable.setNext(x, minNeighbor);
                    routingTable.setDistance(x, minDistanceToX);
                }
            }
        }
        synchronized (router.routingTableLock) {  // Add synchronized (router.routingTableLock)
            routingTable.setDistancesAfterAsDistances();
        }

        for (Neighbor neighbor : router.getNeighborsMap().values()) {
            int neighborName = neighbor.getName();
            int newWeight = CreateInput.weightsMatrix[routerName][neighborName][round];
            neighbor.setEdgeWeight(newWeight);
            newWeight = neighbor.getEdgeWeight();
            for (int x = 1; x <= router.getNumOfRouters(); x++) {
                if (x != routerName) {
                    synchronized (router.routingTableLock) {  // Add synchronized (router.routingTableLock)
                        if (routingTable.getNextRouter(x) == neighborName) {
                            int oldDistance = routingTable.getDistance(x);
                            routingTable.setDistancesAfter(x, oldDistance - neighbor.getOldEdgeWeight() + newWeight);
                        }
                    }
                }
            }
        }
        System.out.println("Incrementing!");
        router.updateNumber.incrementAndGet();
        sendFinish();
    }

    private void shutDown() {

    }

    private void forward() {
        synchronized (router.forwardFileLock) {
            appendToFile(router.getForwardFile(), senderMessage);
        }

        String[] subMessages = senderMessage.split(UdpMessages.FORWARD_MESSAGE_DELIMITER);
        int destination = Integer.parseInt(subMessages[1]);
        int hops = Integer.parseInt(subMessages[2]);
        String messageToForward = subMessages[3];
        String ip = subMessages[4];
        int port = Integer.parseInt(subMessages[5]);

        if (hops == 0 || destination == router.getRouterName()) {
            sendMessage(ip, port, messageToForward);
        } else {
            int nextRouter;

            synchronized (router.routingTableLock) {
                nextRouter = router.getRoutingTable().getNextRouter(destination);
            }

            Neighbor nextAsNeighbor = router.getNeighbor(nextRouter);
            String nextIp = nextAsNeighbor.getIp();
            int nextPort = nextAsNeighbor.getUdpPort();
            String delimiter = UdpMessages.FORWARD_MESSAGE_DELIMITER;

            String reconstructedMessage = myJoin(delimiter, UdpMessages.FORWARD_MESSAGE_PREFIX, destination, hops - 1, messageToForward, ip, port);
            sendMessage(nextIp, nextPort, reconstructedMessage);
        }

    }

    private void sendFinish() {
        byte[] bytesToSend = UdpMessages.FINISH_MESSAGE.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, senderAddress, senderPort);
        try {
            socket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String ip, int port, String message) {
        byte[] bytesToSend = message.getBytes(StandardCharsets.UTF_8);
        try {
            DatagramSocket socket = new DatagramSocket(); // ?!
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, address, port);
            socket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void appendToFile(String fileName, String text) {
        try (FileWriter myWriter = new FileWriter(fileName, true)) {
            myWriter.write(text + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String myJoin(String delimiter, Object... objs) {
        String[] arr = new String[objs.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = objs[i].toString();
        }
        return String.join(delimiter, arr);
    }
}

//class A {
//    public static void main(String[] args) {
//
//        try (FileWriter myWriter = new FileWriter("Hey.txt", true)) {
//            myWriter.write("aaaaaaaaa" + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try (FileWriter myWriter = new FileWriter("Hey.txt", true)) {
//            myWriter.write("bbbbb" + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
