public class Neighbor {
    private final int name;
    private final String ip;
    private final int udpPort;
    private final int tcpPort;
    private final int edgeWeight;

    public Neighbor(int name, String ip, int udpPort, int tcpPort, int edgeWeight) {
        this.name = name;
        this.ip = ip;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.edgeWeight = edgeWeight;
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
}
