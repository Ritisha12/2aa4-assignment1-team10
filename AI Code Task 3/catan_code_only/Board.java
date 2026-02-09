import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> tiles;
    private List<Node> nodes;
    private List<Edge> edges;
    
    public Board() {
        this.tiles = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }
    
    public void initializeBoard() {
        // Create 19 tiles (0-18) following the specification
        // Tile 0: center
        // Tiles 1-6: inner ring
        // Tiles 7-18: outer ring
        
        // Simplified standard Catan setup
        // Center tile (ID 0)
        tiles.add(new Tile(0, ResourceType.WHEAT, 6));
        
        // Inner ring (IDs 1-6)
        tiles.add(new Tile(1, ResourceType.ORE, 5));
        tiles.add(new Tile(2, ResourceType.SHEEP, 10));
        tiles.add(new Tile(3, ResourceType.WOOD, 3));
        tiles.add(new Tile(4, ResourceType.BRICK, 8));
        tiles.add(new Tile(5, ResourceType.WHEAT, 4));
        tiles.add(new Tile(6, ResourceType.SHEEP, 9));
        
        // Outer ring (IDs 7-18)
        tiles.add(new Tile(7, ResourceType.WOOD, 11));
        tiles.add(new Tile(8, ResourceType.BRICK, 12));
        tiles.add(new Tile(9, ResourceType.WHEAT, 6));
        tiles.add(new Tile(10, ResourceType.ORE, 5));
        tiles.add(new Tile(11, ResourceType.SHEEP, 10));
        tiles.add(new Tile(12, ResourceType.DESERT, 0)); // Desert has no number
        tiles.add(new Tile(13, ResourceType.WOOD, 3));
        tiles.add(new Tile(14, ResourceType.BRICK, 8));
        tiles.add(new Tile(15, ResourceType.WHEAT, 4));
        tiles.add(new Tile(16, ResourceType.ORE, 9));
        tiles.add(new Tile(17, ResourceType.SHEEP, 11));
        tiles.add(new Tile(18, ResourceType.WOOD, 2));
        
        // Create nodes (54 nodes in standard Catan)
        // Simplified: create enough nodes for basic gameplay
        for (int i = 0; i < 54; i++) {
            nodes.add(new Node(i));
        }
        
        // Create edges (72 edges in standard Catan)
        // Simplified: create connections between nodes
        for (int i = 0; i < 72; i++) {
            // Simplified edge creation
            int node1Id = i % 54;
            int node2Id = (i + 1) % 54;
            edges.add(new Edge(i, nodes.get(node1Id), nodes.get(node2Id)));
        }
        
        // Set up adjacencies (simplified)
        setupAdjacencies();
    }
    
    private void setupAdjacencies() {
        // Simplified adjacency setup
        // In a real implementation, this would follow the hexagonal grid structure
        
        // Example: Connect nodes around center tile (tile 0)
        // Tile 0 is adjacent to nodes 0-5
        for (int i = 0; i < 6; i++) {
            tiles.get(0).addAdjacentNode(nodes.get(i));
            nodes.get(i).addAdjacentTile(tiles.get(0));
        }
        
        // Connect adjacent nodes
        for (int i = 0; i < 6; i++) {
            int next = (i + 1) % 6;
            nodes.get(i).addAdjacentNode(nodes.get(next));
            nodes.get(next).addAdjacentNode(nodes.get(i));
        }
        
        // Set up more adjacencies for inner ring tiles (1-6)
        for (int tileId = 1; tileId <= 6; tileId++) {
            int baseNode = tileId * 6;
            for (int i = 0; i < 6; i++) {
                int nodeId = baseNode + i;
                if (nodeId < nodes.size()) {
                    tiles.get(tileId).addAdjacentNode(nodes.get(nodeId));
                    nodes.get(nodeId).addAdjacentTile(tiles.get(tileId));
                }
            }
        }
        
        // Connect nodes within each tile's boundary
        for (int tileId = 1; tileId <= 6; tileId++) {
            int baseNode = tileId * 6;
            for (int i = 0; i < 6; i++) {
                int nodeId = baseNode + i;
                int nextId = baseNode + ((i + 1) % 6);
                if (nodeId < nodes.size() && nextId < nodes.size()) {
                    nodes.get(nodeId).addAdjacentNode(nodes.get(nextId));
                    nodes.get(nextId).addAdjacentNode(nodes.get(nodeId));
                }
            }
        }
    }
    
    public Tile getTileById(int id) {
        for (Tile tile : tiles) {
            if (tile.getId() == id) {
                return tile;
            }
        }
        return null;
    }
    
    public Node getNodeById(int id) {
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
    
    public Edge getEdgeById(int id) {
        for (Edge edge : edges) {
            if (edge.getId() == id) {
                return edge;
            }
        }
        return null;
    }
    
    public List<Tile> getAdjacentTiles(Node node) {
        return node.getAdjacentTiles();
    }
    
    // Getters
    public List<Tile> getTiles() { 
        return new ArrayList<>(tiles); 
    }
    
    public List<Node> getNodes() { 
        return new ArrayList<>(nodes); 
    }
    
    public List<Edge> getEdges() { 
        return new ArrayList<>(edges); 
    }
}
