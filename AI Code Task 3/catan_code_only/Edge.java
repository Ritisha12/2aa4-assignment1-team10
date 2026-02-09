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
        
        // Register this edge with both nodes
        if (node1 != null) {
            node1.addAdjacentEdge(this);
        }
        if (node2 != null) {
            node2.addAdjacentEdge(this);
        }
    }
    
    public boolean canBuildRoad(Player player) {
        // Check if edge already has a road
        if (road != null) {
            return false;
        }
        
        // Check if player has adjacent road or building
        // Check node1
        if (node1.getBuilding() != null && 
            node1.getBuilding().getOwner().equals(player)) {
            return true;
        }
        
        // Check node2
        if (node2.getBuilding() != null && 
            node2.getBuilding().getOwner().equals(player)) {
            return true;
        }
        
        // Check adjacent edges for player's roads
        for (Edge edge : node1.getAdjacentEdges()) {
            if (edge.getRoad() != null && 
                edge.getRoad().getOwner().equals(player) &&
                edge != this) {
                return true;
            }
        }
        
        for (Edge edge : node2.getAdjacentEdges()) {
            if (edge.getRoad() != null && 
                edge.getRoad().getOwner().equals(player) &&
                edge != this) {
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
    
    public boolean connectsToPlayer(Player player) {
        // Check if either node has player's building
        if (node1.getBuilding() != null && 
            node1.getBuilding().getOwner().equals(player)) {
            return true;
        }
        if (node2.getBuilding() != null && 
            node2.getBuilding().getOwner().equals(player)) {
            return true;
        }
        return false;
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public Road getRoad() { 
        return road; 
    }
    
    public Node getNode1() { 
        return node1; 
    }
    
    public Node getNode2() { 
        return node2; 
    }
    
    @Override
    public String toString() {
        String roadStr = road != null ? road.toString() : "empty";
        return String.format("Edge[id=%d, road=%s]", id, roadStr);
    }
}
