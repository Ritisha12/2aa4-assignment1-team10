package CatanSimulator;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    private int id;
    private ResourceType resourceType;
    private int numberToken;
    private List<Node> adjacentNodes;

    public Tile(int id, ResourceType resourceType, int numberToken) {
        this.id = id;
        this.resourceType = resourceType;
        this.numberToken = numberToken;
        this.adjacentNodes = new ArrayList<>();
    }

    // returns the resource this tile produces, or null if desert
    public ResourceType produceResource() {
        if (resourceType == ResourceType.DESERT) return null;
        return resourceType;
    }

    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) {
            adjacentNodes.add(node);
        }
    }

    public int getId() { return id; }
    public ResourceType getResourceType() { return resourceType; }
    public int getNumberToken() { return numberToken; }
    public List<Node> getAdjacentNodes() { return adjacentNodes; }

    @Override
    public String toString() {
        return "Tile[" + id + ", " + resourceType + ", token=" + numberToken + "]";
    }
}
