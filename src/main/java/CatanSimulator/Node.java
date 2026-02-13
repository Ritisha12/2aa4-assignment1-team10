package CatanSimulator;

import java.util.ArrayList;
import java.util.List;

// represents an intersection on the board where buildings can be placed
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

    // check if a player can build a settlement here
    // distance rule: no adjacent node can have a building
    // also needs road connectivity
    public boolean canBuildSettlement(Player player) {
        if (building != null) return false;

        // check distance rule
        for (Node adj : adjacentNodes) {
            if (adj.getBuilding() != null) return false;
        }

        // check road connectivity - player needs a road on adjacent edge
        for (Edge edge : adjacentEdges) {
            if (edge.getRoad() != null && edge.getRoad().getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    // for initial placement - only distance rule, no road needed
    public boolean canBuildInitialSettlement() {
        if (building != null) return false;
        for (Node adj : adjacentNodes) {
            if (adj.getBuilding() != null) return false;
        }
        return true;
    }

    // check if player can upgrade their settlement to a city
    public boolean canUpgradeToCity(Player player) {
        return building instanceof Settlement && building.getOwner().equals(player);
    }

    public boolean placeBuilding(Building b) {
        if (this.building == null) {
            this.building = b;
            return true;
        }
        return false;
    }

    public boolean upgradeToCity(Building city) {
        if (building instanceof Settlement && city.getOwner().equals(building.getOwner())) {
            this.building = city;
            return true;
        }
        return false;
    }

    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) adjacentNodes.add(node);
    }

    public void addAdjacentTile(Tile tile) {
        if (!adjacentTiles.contains(tile)) adjacentTiles.add(tile);
    }

    public void addAdjacentEdge(Edge edge) {
        if (!adjacentEdges.contains(edge)) adjacentEdges.add(edge);
    }

    public int getId() { return id; }
    public Building getBuilding() { return building; }
    public List<Node> getAdjacentNodes() { return adjacentNodes; }
    public List<Tile> getAdjacentTiles() { return adjacentTiles; }
    public List<Edge> getAdjacentEdges() { return adjacentEdges; }

    @Override
    public String toString() {
        String bStr = (building != null) ? building.toString() : "empty";
        return "Node[id=" + id + ", building=" + bStr + "]";
    }
}
