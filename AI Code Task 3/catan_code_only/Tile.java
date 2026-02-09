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
    
    public ResourceType produceResource() {
        if (hasRobber || resourceType == ResourceType.DESERT) {
            return null;
        }
        return resourceType;
    }
    
    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) {
            adjacentNodes.add(node);
        }
    }
    
    // Getters and Setters
    public int getId() { 
        return id; 
    }
    
    public ResourceType getResourceType() { 
        return resourceType; 
    }
    
    public int getNumberToken() { 
        return numberToken; 
    }
    
    public boolean hasRobber() { 
        return hasRobber; 
    }
    
    public void setHasRobber(boolean hasRobber) { 
        this.hasRobber = hasRobber; 
    }
    
    public List<Node> getAdjacentNodes() {
        return new ArrayList<>(adjacentNodes);
    }
    
    @Override
    public String toString() {
        return String.format("Tile[id=%d, type=%s, number=%d, robber=%b]", 
                           id, resourceType, numberToken, hasRobber);
    }
}
