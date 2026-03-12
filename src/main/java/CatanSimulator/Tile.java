package CatanSimulator;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    private int id;
    private ResourceType resourceType;
    private int numberToken;
    private boolean hasRobber;
    private List<Node> adjacentNodes;

    public Tile(int id, ResourceType resourceType, int numberToken) {
        this.id = id;
        this.resourceType = resourceType;
        this.numberToken = numberToken;
        this.hasRobber = false;
        this.adjacentNodes = new ArrayList<>();
    }

    // returns the resource this tile produces, or null if desert
    public ResourceType produceResource() {
        if (resourceType == ResourceType.DESERT || hasRobber) return null;
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
    public boolean hasRobber() { return hasRobber; }
    public void setRobber(boolean hasRobber) { this.hasRobber = hasRobber; }

    @Override
    public String toString() {
        return "Tile[" + id + ", " + resourceType + ", token=" + numberToken + "]";
    }
}
