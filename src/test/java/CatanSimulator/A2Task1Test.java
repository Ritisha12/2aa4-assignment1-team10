package CatanSimulator;

//imports
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Assignment 2 - Task 1: Unit Tests for Catan Simulator
 *
 * Test suites:
 *   - ResourceHandTest  : tests for ResourceHand which includes partition and boundary testing
 *   - NodeTest          : tests for Node placement rules
 *   - PlayerTest        : tests for Player VP, card count, initial placement
 *   - BoardTest         : tests for Board structure after generateMap()
 *
 */
public class A2Task1Test {

    // SUITE 1: ResourceHand Tests

    @Nested
    @DisplayName("ResourceHand Tests")
    class ResourceHandTest {

        private ResourceHand hand;

        @BeforeEach
        void setUp() {
            hand = new ResourceHand();
        }

        @Test
        @DisplayName("New hand starts with zero total cards")
        void testNewHandIsEmpty() {
            assertEquals(0, hand.getTotalCards());
        }

        @Test
        @DisplayName("Adding resources increases total correctly")
        void testAddResource() {
            hand.addResource(ResourceType.WOOD, 2);
            hand.addResource(ResourceType.BRICK, 3);
            assertEquals(5, hand.getTotalCards());
        }

        @Test
        @DisplayName("removeResource fails when insufficient cards")
        void testRemoveResourceFail() {
            hand.addResource(ResourceType.WHEAT, 1);
            boolean result = hand.removeResource(ResourceType.WHEAT, 2);
            assertFalse(result);
        }

        /**
         * PARTITION TESTING:
         * Partitions based on 7-card limit:
         *   - Partition A: 0 cards       (empty hand)
         *   - Partition B: 1-7 cards     (normal, no forced build)
         *   - Partition C: 8+ cards      (over limit)
         */
        @Test
        @DisplayName("Partition A: 0 cards - hasMoreThanCards(7) is false")
        void testPartitionEmptyHand() {
            Player p = new Player(1, "TestPlayer");
            assertFalse(p.hasMoreThanCards(7));
        }

        @Test
        @DisplayName("Partition B: 7 cards exactly - not over limit")
        void testPartitionExactlySevenCards() {
            hand.addResource(ResourceType.WOOD, 3);
            hand.addResource(ResourceType.BRICK, 4);
            assertFalse(hand.getTotalCards() > 7);
        }

        @Test
        @DisplayName("Partition C: 8 cards - hasMoreThanCards(7) is true")
        void testPartitionOverSevenCards() {
            Player p = new Player(1, "TestPlayer");
            p.getResources().addResource(ResourceType.WOOD, 4);
            p.getResources().addResource(ResourceType.BRICK, 4);
            assertTrue(p.hasMoreThanCards(7));
        }

        /**
         * BOUNDARY TESTING:
         * Boundary at 7-card limit:
         *   - 7 cards: at boundary -> not over
         *   - 8 cards: just over   -> over limit
         */
        @Test
        @DisplayName("Boundary: 7 cards - exactly at limit, not over")
        void testBoundaryAtLimit() {
            hand.addResource(ResourceType.SHEEP, 7);
            assertFalse(hand.getTotalCards() > 7);
        }

        @Test
        @DisplayName("Boundary: 8 cards - just over limit")
        void testBoundaryOverLimit() {
            hand.addResource(ResourceType.SHEEP, 8);
            assertTrue(hand.getTotalCards() > 7);
        }
    }

    // SUITE 2: Node Tests
    @Nested
    @DisplayName("Node Tests")
    class NodeTest {

        @Test
        @DisplayName("New node has no building")
        void testNewNodeIsEmpty() {
            Node node = new Node(0);
            assertNull(node.getBuilding());
        }

        @Test
        @DisplayName("Distance rule: cannot build initial settlement adjacent to another")
        void testDistanceRuleViolation() {
            Node node1 = new Node(0);
            Node node2 = new Node(1);
            node1.addAdjacentNode(node2);
            node2.addAdjacentNode(node1);
            Player p = new Player(1, "Alice");
            node1.placeBuilding(new Settlement(p));
            assertFalse(node2.canBuildInitialSettlement());
        }

        @Test
        @DisplayName("Can't place second building on already occupied node")
        void testCannotDoublePlace() {
            Node node = new Node(0);
            Player p = new Player(1, "Alice");
            node.placeBuilding(new Settlement(p));
            assertFalse(node.placeBuilding(new Settlement(p)));
        }

        @Test
        @DisplayName("canUpgradeToCity is false when another player owns the settlement")
        void testCannotUpgradeCityOfOtherPlayer() {
            Node node = new Node(0);
            Player p1 = new Player(1, "Alice");
            Player p2 = new Player(2, "Bob");
            node.placeBuilding(new Settlement(p1));
            assertFalse(node.canUpgradeToCity(p2));
        }

        @Test
        @DisplayName("upgradeToCity replaces settlement with city")
        void testUpgradeToCitySucceeds() {
            Node node = new Node(0);
            Player p = new Player(1, "Alice");
            node.placeBuilding(new Settlement(p));
            assertTrue(node.upgradeToCity(new City(p)));
            assertInstanceOf(City.class, node.getBuilding());
        }
    }


    // SUITE 3: Player Test
    @Nested
    @DisplayName("Player Tests")
    class PlayerTest {

        @Test
        @DisplayName("New player starts with 0 victory points")
        void testInitialVP() {
            Player p = new Player(1, "Alice");
            assertEquals(0, p.getVictoryPoints());
        }

        @Test
        @DisplayName("New player starts with 0 roads, settlements, cities")
        void testInitialCounts() {
            Player p = new Player(1, "Alice");
            assertEquals(0, p.getRoadCount());
            assertEquals(0, p.getSettlementCount());
            assertEquals(0, p.getCityCount());
        }

        @Test
        @DisplayName("placeInitialSettlement increases VP by 1")
        void testInitialSettlementAddsVP() {
            Player p = new Player(1, "Alice");
            p.placeInitialSettlement(new Node(0));
            assertEquals(1, p.getVictoryPoints());
        }

        @Test
        @DisplayName("Player ID and name stored correctly")
        void testPlayerIdAndName() {
            Player p = new Player(3, "Charlie");
            assertEquals(3, p.getId());
            assertEquals("Charlie", p.getName());
        }
    }

    // SUITE 4: Board Tests

    @Nested
    @DisplayName("Board Tests")
    class BoardTest {

        private Board board;

        @BeforeEach
        void setUp() {
            board = new Board();
            board.generateMap();
        }

        @Test
        @DisplayName("Board has exactly 19 tiles after generateMap()")
        void testTileCount() {
            assertEquals(19, board.getTiles().size());
        }

        @Test
        @DisplayName("Board has exactly 54 nodes after generateMap()")
        void testNodeCount() {
            assertEquals(54, board.getNodes().size());
        }

        @Test
        @DisplayName("Each tile has exactly 6 adjacent nodes")
        void testEachTileHasSixNodes() {
            for (Tile tile : board.getTiles()) {
                assertEquals(6, tile.getAdjacentNodes().size());
            }
        }
    }
}
