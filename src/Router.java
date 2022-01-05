import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Router extends Thread {
    private final int name;
    private int udpPort;
    private int tcpPort;
    private int numOfRouters;
    private int diameterBound;  // TODO: Maybe delete the field, and make initializeFromFile return the bound.
    private int firstNeighbor;
    private RoutingTable routingTable;
    private Map<Integer, Neighbor> neighborsMap;

    public Router(int name, String inputFilePrefix, String tableFilePrefix, String forwardingFilePrefix) {
        this.name = name;
        String inputFileName = inputFilePrefix + name + ".txt";
        neighborsMap = new HashMap<>();
        initializeFromFile(inputFileName);
        routingTable = new RoutingTable(numOfRouters, name, diameterBound, firstNeighbor);
    }

    @Override
    public void run() {
        // TODO: implement run
        UdpListener u = new UdpListener(udpPort, name);
        u.start();
    }

    private void initializeFromFile(String fileName) {
        File inputFile = new File(fileName);
        try {
            Scanner scanner = new Scanner(inputFile);
            udpPort = Integer.parseInt(scanner.nextLine());
            tcpPort = Integer.parseInt(scanner.nextLine());
            numOfRouters = Integer.parseInt(scanner.nextLine());
            boolean stop = false;
            boolean isFirstNeighbor = true;
            while (!stop) {
                String nextLine = scanner.nextLine();
                if (nextLine.equals("*")) {
                    stop = true;
                } else {
                    int neighborName = Integer.parseInt(nextLine);
                    if (isFirstNeighbor) {
                        firstNeighbor = neighborName;
                        isFirstNeighbor = false;
                    }
                    String neighborIP = scanner.nextLine();
                    int neighborUDPPort = Integer.parseInt(scanner.nextLine());
                    int neighborTCPPort = Integer.parseInt(scanner.nextLine());
                    int neighborEdgeWeight = Integer.parseInt(scanner.nextLine());
                    Neighbor neighbor = new Neighbor(neighborName, neighborIP, neighborUDPPort, neighborTCPPort, neighborEdgeWeight);
                    neighborsMap.put(neighborName, neighbor);
                }
            }
            this.diameterBound = Integer.parseInt(scanner.nextLine());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
