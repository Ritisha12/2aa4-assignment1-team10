package CatanSimulator;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> tiles;
    private List<Node> nodes;
    private List<Edge> edges;

    public Board() {
        tiles = new ArrayList<>();
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    // builds the entire board from the MapSetup data
    public void generateMap() {
        // create all 54 nodes
        for (int i = 0; i < MapSetup.NUM_NODES; i++) {
            nodes.add(new Node(i));
        }

        // create the 19 tiles
        for (int i = 0; i < MapSetup.NUM_TILES; i++) {
            tiles.add(new Tile(i, MapSetup.TILE_RESOURCES[i], MapSetup.TILE_NUMBERS[i]));
        }

        // hook up tile-node adjacencies
        for (int t = 0; t < MapSetup.NUM_TILES; t++) {
            Tile tile = tiles.get(t);
            int[] nodeIds = MapSetup.TILE_NODES[t];
            for (int nId : nodeIds) {
                Node node = nodes.get(nId);
                tile.addAdjacentNode(node);
                node.addAdjacentTile(tile);
            }
        }

        // figure out edges from consecutive hex vertices
        // keep track of which pairs we already made so we dont duplicate
        ArrayList<String> createdEdges = new ArrayList<>();
        int edgeId = 0;

        for (int t = 0; t < MapSetup.NUM_TILES; t++) {
            int[] hexNodes = MapSetup.TILE_NODES[t];
            for (int i = 0; i < 6; i++) {
                int n1 = hexNodes[i];
                int n2 = hexNodes[(i + 1) % 6];

                // make a string key like "3-17" so we can check for duplicates
                int small = Math.min(n1, n2);
                int big = Math.max(n1, n2);
                String key = small + "-" + big;

                if (!createdEdges.contains(key)) {
                    createdEdges.add(key);
                    Node node1 = nodes.get(n1);
                    Node node2 = nodes.get(n2);
                    node1.addAdjacentNode(node2);
                    node2.addAdjacentNode(node1);
                    edges.add(new Edge(edgeId++, node1, node2));
                }
            }
        }
    }

    public List<Tile> getTiles() { return tiles; }
    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
}
