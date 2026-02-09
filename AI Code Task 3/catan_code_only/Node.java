import java.util.ArrayList;
import java.util.List;

public class Node {
    private int id;
    private Building building;
    private List<Node> adjacentNodes;
    private List<Tile> adjacentTiles;
    private List<Edge> adjacentEdges;
    
    public Node(int id) {
        this.id = id;
        this.building = null;
        this.adjacentNodes = new ArrayList<>();
        this.adjacentTiles = new ArrayList<>();
        this.adjacentEdges = new ArrayList<>();
    }
    
    public boolean canBuildSettlement(Player player) {
        // Check if node is empty
        if (building != null) {
            return false;
        }
        
        // Check distance rule: adjacent nodes must be empty
        for (Node adjacent : adjacentNodes) {
            if (adjacent.getBuilding() != null) {
                return false;
            }
        }
        
        // Check if player has connection (road or settlement nearby)
        boolean hasConnection = false;
        for (Edge edge : adjacentEdges) {
            if (edge.getRoad() != null && edge.getRoad().getOwner().equals(player)) {
                hasConnection = true;
                break;
            }
        }
        
        // For initial placement, connection not required (handled separately)
        return true;
    }
    
    public boolean canUpgradeToCity(Player player) {
        if (building == null) {
            return false;
        }
        if (!(building instanceof Settlement)) {
            return false;
        }
        Settlement settlement = (Settlement) building;
        return settlement.getOwner().equals(player);
    }
    
    public boolean placeBuilding(Building building) {
        if (this.building == null) {
            this.building = building;
            return true;
        }
        return false;
    }
    
    public boolean upgradeToCity(Building city) {
        if (this.building instanceof Settlement) {
            Settlement oldSettlement = (Settlement) this.building;
            if (city.getOwner().equals(oldSettlement.getOwner())) {
                this.building = city;
                return true;
            }
        }
        return false;
    }
    
    public boolean isOccupied() {
        return building != null;
    }
    
    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) {
            adjacentNodes.add(node);
        }
    }
    
    public void addAdjacentTile(Tile tile) {
        if (!adjacentTiles.contains(tile)) {
            adjacentTiles.add(tile);
        }
    }
    
    public void addAdjacentEdge(Edge edge) {
        if (!adjacentEdges.contains(edge)) {
            adjacentEdges.add(edge);
        }
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public Building getBuilding() { 
        return building; 
    }
    
    public List<Node> getAdjacentNodes() { 
        return new ArrayList<>(adjacentNodes); 
    }
    
    public List<Tile> getAdjacentTiles() { 
        return new ArrayList<>(adjacentTiles); 
    }
    
    public List<Edge> getAdjacentEdges() {
        return new ArrayList<>(adjacentEdges);
    }
    
    @Override
    public String toString() {
        String buildingStr = building != null ? building.toString() : "empty";
        return String.format("Node[id=%d, building=%s]", id, buildingStr);
    }
}
