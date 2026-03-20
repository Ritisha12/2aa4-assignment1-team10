package CatanSimulator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2StrategyTest {
    @Test
    @DisplayName("AI prefers a city upgrade over a road when both are available")
    void aiChoosesHighestValueMove() {
        Board board = new Board();
        board.generateMap();

        AIPlayer ai = new AIPlayer(1, "AI", PlayerColor.BLUE);
        Player rival = new HumanPlayer(0, "Rival", PlayerColor.RED, null, System.out);
        Node cityNode = board.getNodeById(0);
        ai.placeInitialSettlement(cityNode);

        Edge firstRoad = board.getEdgeByNodeIds(0, 1);
        Edge secondRoad = board.getEdgeByNodeIds(1, 2);
        firstRoad.placeRoad(new Road(ai, firstRoad));
        secondRoad.placeRoad(new Road(ai, secondRoad));
        ai.addRoad(new Road(ai, firstRoad));
        ai.addRoad(new Road(ai, secondRoad));

        ai.getResources().addResource(ResourceType.WHEAT, 2);
        ai.getResources().addResource(ResourceType.ORE, 3);
        ai.getResources().addResource(ResourceType.WOOD, 1);
        ai.getResources().addResource(ResourceType.BRICK, 1);

        boolean moved = ai.takeStrategicTurn(board, List.of(rival, ai), new ActionLogger(), 1);

        assertTrue(moved);
        assertTrue(cityNode.getBuilding() instanceof City);
        assertEquals(1, ai.getCityCount());
        assertEquals(2, ai.getRoadCount());
    }

    @Test
    @DisplayName("AI starts connecting road segments before taking a higher-value city action")
    void aiPrioritizesConnectingRoadSegments() {
        Board board = new Board();
        board.generateMap();

        AIPlayer ai = new AIPlayer(1, "AI", PlayerColor.BLUE);
        Node cityNode = board.getNodeById(10);
        ai.placeInitialSettlement(cityNode);

        Edge first = board.getEdgeByNodeIds(0, 1);
        Edge second = board.getEdgeByNodeIds(3, 4);
        first.placeRoad(new Road(ai, first));
        second.placeRoad(new Road(ai, second));
        ai.addRoad(new Road(ai, first));
        ai.addRoad(new Road(ai, second));

        ai.getResources().addResource(ResourceType.WHEAT, 2);
        ai.getResources().addResource(ResourceType.ORE, 3);
        ai.getResources().addResource(ResourceType.WOOD, 1);
        ai.getResources().addResource(ResourceType.BRICK, 1);

        boolean moved = ai.takeStrategicTurn(board, List.of(ai), new ActionLogger(), 1);

        assertTrue(moved);
        assertTrue(board.getEdgeByNodeIds(1, 2).getRoad() != null
            || board.getEdgeByNodeIds(2, 3).getRoad() != null);
        assertTrue(cityNode.getBuilding() instanceof Settlement);
        assertEquals(3, ai.getRoadCount());
    }

    @Test
    @DisplayName("AI defends longest road before taking a city upgrade")
    void aiDefendsLongestRoadLead() {
        Board board = new Board();
        board.generateMap();

        AIPlayer ai = new AIPlayer(1, "AI", PlayerColor.BLUE);
        Player rival = new HumanPlayer(0, "Rival", PlayerColor.RED, null, System.out);
        Node cityNode = board.getNodeById(10);
        ai.placeInitialSettlement(cityNode);

        Edge aiRoadOne = board.getEdgeByNodeIds(0, 1);
        Edge aiRoadTwo = board.getEdgeByNodeIds(1, 2);
        aiRoadOne.placeRoad(new Road(ai, aiRoadOne));
        aiRoadTwo.placeRoad(new Road(ai, aiRoadTwo));
        ai.addRoad(new Road(ai, aiRoadOne));
        ai.addRoad(new Road(ai, aiRoadTwo));

        Edge rivalRoad = board.getEdgeByNodeIds(3, 4);
        rivalRoad.placeRoad(new Road(rival, rivalRoad));
        rival.addRoad(new Road(rival, rivalRoad));

        ai.getResources().addResource(ResourceType.WHEAT, 2);
        ai.getResources().addResource(ResourceType.ORE, 3);
        ai.getResources().addResource(ResourceType.WOOD, 1);
        ai.getResources().addResource(ResourceType.BRICK, 1);

        boolean moved = ai.takeStrategicTurn(board, List.of(rival, ai), new ActionLogger(), 1);

        assertTrue(moved);
        assertTrue(board.getEdgeByNodeIds(2, 3).getRoad() != null
            || board.getEdgeByNodeIds(5, 0).getRoad() != null);
        assertTrue(cityNode.getBuilding() instanceof Settlement);
        assertEquals(3, ai.getRoadCount());
    }
}
