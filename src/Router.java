import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Router extends Thread {
    private final int name;
    private int udpPort;
    private int tcpPort;
    private int numOfRouters;
    private int diameterBound;  // TODO: Maybe delete the field, and make initializeFromFile return the bound.
    private int firstNeighbor;
    private final String tableFile;
    private final String forwardFile;
    private RoutingTable routingTable;
    private Map<Integer, Neighbor> neighborsMap;
    public AtomicInteger updateNumber;

    // Locks
    public final Object routingTableLock = new Object();
    public final Object forwardFileLock = new Object();
    public final Object tableFileLock = new Object();

    public Router(int name, String inputFilePrefix, String tableFilePrefix, String forwardingFilePrefix) {
        this.name = name;
        String inputFileName = inputFilePrefix + name + ".txt";
        tableFile = tableFilePrefix + name + ".txt";
        forwardFile = forwardingFilePrefix + name + ".txt";
        neighborsMap = new HashMap<>();
        initializeFromFile(inputFileName);
        routingTable = new RoutingTable(numOfRouters, name, diameterBound, firstNeighbor);
        buildFirstDistancesVector();
        updateNumber = new AtomicInteger(1);

    }

    @Override
    public void run() {
        // TODO: implement run
        UdpListener u = new UdpListener(this);  // TODO: Delete it (just for testing if udp listeners get message from clients)
        u.start();
    }

    public int getRouterName() {
        return name;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public Map<Integer, Neighbor> getNeighborsMap() {
        return neighborsMap;
    }

    public String getTableFile() {
        return tableFile;
    }

    public String getForwardFile() {
        return forwardFile;
    }

    public int getNumOfRouters() {
        return numOfRouters;
    }

    public Neighbor getNeighbor(int neighborName) {
        return neighborsMap.get(neighborName);
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

    private void buildFirstDistancesVector() {
        for (Neighbor neighbor : neighborsMap.values()) {
            int neighborName = neighbor.getName();
            int newWeight = CreateInput.weightsMatrix[neighborName][neighborName][1];
            neighbor.setEdgeWeight(newWeight);
            for (int x = 1; x < numOfRouters; x++) {
                if (routingTable.getNextRouter(x) == neighborName) {
                    int oldDistance = routingTable.getDistance(x);
                    routingTable.setDistance(x, oldDistance - neighbor.getOldEdgeWeight() + newWeight);
                }
            }
        }
    }
}
