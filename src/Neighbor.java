public class Neighbor {  // TODO: (Maybe) Add synchronized to edgeWeight getter and setter
    private final int name;     // TODO: Maybe delete the field
    private final String ip;
    private final int udpPort;
    private final int tcpPort;
    private int edgeWeight;     // TODO: Maybe delete the field
    private int oldEdgeWeight;  // TODO: Maybe delete the field

    public Neighbor(int name, String ip, int udpPort, int tcpPort, int edgeWeight) {
        this.name = name;
        this.ip = ip;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.edgeWeight = edgeWeight;
        // TODO: Figure out what value to give to oldEdgeWeight field
    }

    public int getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getEdgeWeight() {
        return edgeWeight;
    }

    public int getOldEdgeWeight() {
        return oldEdgeWeight;
    }

    public void setEdgeWeight(int edgeWeight) {
        if (edgeWeight != -1) {
            this.oldEdgeWeight = this.edgeWeight;  // TODO: Figure out what to do in the first update
            this.edgeWeight = edgeWeight;
        } else {
            this.oldEdgeWeight = this.edgeWeight;
        }
        // TODO: Verify if those two lines need to be in the same function, based on line 7 of the algorithm
    }
}
