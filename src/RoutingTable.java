public class RoutingTable {
    private int numOfRouters;
    private int routerName;
    private int[] distances;
    private int[] next;

    public RoutingTable(int numOfRouters, int routerName, int diameterBound, int firstNeighborName) {
        this.numOfRouters = numOfRouters;
        this.routerName = routerName;
        initializeDistances(diameterBound);

    }

    private void initializeDistances(int diameterBound) {
        distances = new int[numOfRouters];
        for (int i = 0; i < numOfRouters; i++) {
            if (i == routerName - 1) {
                distances[i] = 0;
            } else {
                distances[i] = diameterBound;
            }
        }
    }

    private void initializeNext(int firstNeighborName) {
        next = new int[numOfRouters];
        for (int i = 0; i < numOfRouters; i++) {
            if (i == routerName - 1) {
                next[i] = -1;  // TODO: Determine what to put in here
            } else {
                next[i] = firstNeighborName;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numOfRouters; i++) {
            if (i == routerName - 1) {
                sb.append("0;None");
            } else {
                sb.append(distances[i] + ";" + next[i]);  // TODO: Make sure there are no off by one errors!
            }
            if (i < numOfRouters - 1) {  // TODO: Maybe delete this
                sb.append("\n");
            }
        }
    }
}
