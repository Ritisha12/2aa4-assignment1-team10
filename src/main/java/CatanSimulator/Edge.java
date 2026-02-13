package CatanSimulator;

public class Edge {
    private int id;
    private Road road;
    private Node node1;
    private Node node2;

    public Edge(int id, Node node1, Node node2) {
        this.id = id;
        this.node1 = node1;
        this.node2 = node2;
        this.road = null;
        node1.addAdjacentEdge(this);
        node2.addAdjacentEdge(this);
    }

    // checks if the player can place a road here
    public boolean canBuildRoad(Player player) {
        if (road != null) return false;

        // player needs a building or road at one of the endpoints
        if (hasPlayerBuilding(node1, player) || hasPlayerBuilding(node2, player)) {
            return true;
        }
        if (hasAdjacentRoad(node1, player) || hasAdjacentRoad(node2, player)) {
            return true;
        }
        return false;
    }

    private boolean hasPlayerBuilding(Node node, Player player) {
        return node.getBuilding() != null && node.getBuilding().getOwner().equals(player);
    }

    private boolean hasAdjacentRoad(Node node, Player player) {
        for (Edge e : node.getAdjacentEdges()) {
            if (e != this && e.getRoad() != null && e.getRoad().getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean placeRoad(Road road) {
        if (this.road == null) {
            this.road = road;
            return true;
        }
        return false;
    }

    public int getId() { return id; }
    public Road getRoad() { return road; }
    public Node getNode1() { return node1; }
    public Node getNode2() { return node2; }

    @Override
    public String toString() {
        String rStr = (road != null) ? road.toString() : "empty";
        return "Edge[" + id + ", nodes=(" + node1.getId() + "," + node2.getId() + "), road=" + rStr + "]";
    }
}
