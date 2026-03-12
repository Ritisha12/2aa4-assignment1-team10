package CatanSimulator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task3SupportTest {
    private static class TestPlayer extends Player {
        TestPlayer(int id, String name) {
            super(id, name);
        }

        TestPlayer(int id, String name, PlayerColor color) {
            super(id, name, color);
        }
    }

    @Test
    @DisplayName("AI can use a 4:1 bank trade to complete a city upgrade")
    void aiUsesBankTradeToBuildCity() {
        Board board = new Board();
        board.generateMap();

        Player player = new TestPlayer(0, "Player0");
        Node cityNode = board.getNodeById(0);
        player.placeInitialSettlement(cityNode);

        player.getResources().addResource(ResourceType.WOOD, 4);
        player.getResources().addResource(ResourceType.WHEAT, 2);
        player.getResources().addResource(ResourceType.ORE, 2);

        boolean built = player.tryBankTradeForBuild(board, new ActionLogger(), 1);

        assertTrue(built);
        assertEquals(0, player.getResources().getWood());
        assertEquals(0, player.getResources().getWheat());
        assertEquals(0, player.getResources().getOre());
        assertEquals(1, player.getCityCount());
        assertTrue(cityNode.getBuilding() instanceof City);
    }

    @Test
    @DisplayName("AI does not trade when more than one resource is missing")
    void aiSkipsTradeWhenDeficitIsTooLarge() {
        Board board = new Board();
        board.generateMap();

        Player player = new TestPlayer(0, "Player0");
        Player blocker = new TestPlayer(1, "Blocker");
        Node cityNode = board.getNodeById(0);
        player.placeInitialSettlement(cityNode);
        for (Edge edge : cityNode.getAdjacentEdges()) {
            edge.placeRoad(new Road(blocker, edge));
        }

        player.getResources().addResource(ResourceType.WOOD, 4);
        player.getResources().addResource(ResourceType.WHEAT, 1);
        player.getResources().addResource(ResourceType.ORE, 1);

        boolean built = player.tryBankTradeForBuild(board, new ActionLogger(), 1);

        assertFalse(built);
        assertEquals(4, player.getResources().getWood());
        assertTrue(cityNode.getBuilding() instanceof Settlement);
    }

    @Test
    @DisplayName("Exporter writes empty visualizer arrays for a fresh board")
    void exporterWritesEmptyState(@TempDir Path tempDir) throws Exception {
        Board board = new Board();
        board.generateMap();

        Path outputPath = tempDir.resolve("state.json");
        new GameStateExporter(outputPath).export(board);

        String json = Files.readString(outputPath);
        assertTrue(json.contains("\"roads\": ["));
        assertTrue(json.contains("\"buildings\": ["));
        assertFalse(json.contains("\"owner\":"));
    }

    @Test
    @DisplayName("Exporter writes roads and buildings in instructor state.json format")
    void exporterWritesVisualizerSchema(@TempDir Path tempDir) throws Exception {
        Board board = new Board();
        board.generateMap();

        Player red = new TestPlayer(0, "Red", PlayerColor.RED);
        Player blue = new TestPlayer(1, "Blue", PlayerColor.BLUE);

        Node cityNode = board.getNodeById(0);
        cityNode.placeBuilding(new Settlement(red));
        cityNode.upgradeToCity(new City(red));

        Node settlementNode = board.getNodeById(10);
        settlementNode.placeBuilding(new Settlement(blue));

        Edge edge = board.getEdges().get(0);
        edge.placeRoad(new Road(red, edge));

        Path outputPath = tempDir.resolve("state.json");
        new GameStateExporter(outputPath).export(board);

        String json = Files.readString(outputPath);
        int nodeA = Math.min(edge.getNode1().getId(), edge.getNode2().getId());
        int nodeB = Math.max(edge.getNode1().getId(), edge.getNode2().getId());

        assertTrue(json.contains("{ \"a\": " + nodeA + ", \"b\": " + nodeB + ", \"owner\": \"RED\" }"));
        assertTrue(json.contains("{ \"node\": 0, \"owner\": \"RED\", \"type\": \"CITY\" }"));
        assertTrue(json.contains("{ \"node\": 10, \"owner\": \"BLUE\", \"type\": \"SETTLEMENT\" }"));
    }
}
