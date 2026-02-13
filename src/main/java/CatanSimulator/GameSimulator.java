package CatanSimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// main game simulation class
// handles the game loop, initial placements, win conditions, etc
public class GameSimulator {
    private int currentRound;
    private int maxRounds;
    private Board board;
    private List<Player> players;
    private Dice dice;
    private ActionLogger logger;
    private Random random;

    public GameSimulator() {
        currentRound = 0;
        maxRounds = 100;
        board = new Board();
        players = new ArrayList<>();
        dice = new Dice();
        logger = new ActionLogger();
        random = new Random();

        // create 4 players
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i, "Player" + i));
        }
    }

    public void loadConfig(String filename) {
        ConfigurationReader reader = new ConfigurationReader();
        maxRounds = reader.readConfig(filename);
        System.out.println("Loaded config: max rounds = " + maxRounds);
    }

    public void runSimulation() {
        board.generateMap();
        System.out.println("Board initialized: " + board.getTiles().size() + " tiles, "
            + board.getNodes().size() + " nodes, " + board.getEdges().size() + " edges.\n");

        // do initial placements first
        performInitialPlacements();

        // main game loop
        boolean gameWon = false;
        while (currentRound < maxRounds && !gameWon) {
            currentRound++;
            System.out.println("\n=== Round " + currentRound + " ===");

            for (Player player : players) {
                int diceRoll = player.rollDice(dice);
                logger.logAction(currentRound, player, "Rolled " + diceRoll);

                if (diceRoll == 7) {
                    // no resources on a 7
                    logger.logAction(currentRound, player, "Rolled 7 - no resources produced");
                } else {
                    // all players collect resources
                    for (Player p : players) {
                        int before = p.getResources().getTotalCards();
                        p.collectResources(diceRoll, board);
                        int after = p.getResources().getTotalCards();
                        if (after > before) {
                            logger.logAction(currentRound, p,
                                "Collected " + (after - before) + " resources from roll " + diceRoll);
                        }
                    }
                }

                // try to build
                player.takeTurn(board, logger, currentRound);

                // if player has more than 7 cards, keep trying to build
                while (player.hasMoreThanCards(7)) {
                    logger.logAction(currentRound, player,
                        "Has " + player.getResources().getTotalCards() + " cards (>7), attempting to build...");
                    boolean built = player.tryToBuild(board, logger, currentRound);
                    if (!built) break;
                }

                // check if someone won
                if (player.getVictoryPoints() >= 10) {
                    System.out.println("\n*** WINNER: " + player.getName()
                        + " with " + player.getVictoryPoints() + " victory points! ***");
                    gameWon = true;
                    break;
                }
            }

            // print VP at end of round
            logger.logVictoryPoints(currentRound, players);
        }

        printGameState();
    }

    // initial placements: each player gets 2 settlements + 2 roads for free
    // first round forward, second round reverse
    private void performInitialPlacements() {
        System.out.println("=== Initial Placements ===");

        for (Player player : players) {
            placeInitialSettlementAndRoad(player, false);
        }

        // second settlement in reverse order, also collect starting resources
        List<Player> reversed = new ArrayList<>(players);
        Collections.reverse(reversed);
        for (Player player : reversed) {
            placeInitialSettlementAndRoad(player, true);
        }

        System.out.println("=== Initial Placements Complete ===\n");
    }

    private void placeInitialSettlementAndRoad(Player player, boolean collectResources) {
        // find valid spots (distance rule only)
        List<Node> validNodes = new ArrayList<>();
        for (Node node : board.getNodes()) {
            if (node.canBuildInitialSettlement()) validNodes.add(node);
        }

        if (validNodes.isEmpty()) {
            logger.logAction(0, player, "No valid node for initial settlement");
            return;
        }

        Node chosen = validNodes.get(random.nextInt(validNodes.size()));
        player.placeInitialSettlement(chosen);
        logger.logAction(0, player, "Placed initial settlement at node " + chosen.getId());

        if (collectResources) {
            player.collectStartingResources(chosen);
        }

        // place a road next to the settlement
        List<Edge> validEdges = new ArrayList<>();
        for (Edge edge : chosen.getAdjacentEdges()) {
            if (edge.getRoad() == null) validEdges.add(edge);
        }
        if (!validEdges.isEmpty()) {
            Edge roadEdge = validEdges.get(random.nextInt(validEdges.size()));
            player.placeInitialRoad(roadEdge);
            logger.logAction(0, player, "Placed initial road at edge " + roadEdge.getId());
        }
    }

    public void printGameState() {
        System.out.println("\n==================================================");
        System.out.println("  GAME ENDED");
        System.out.println("==================================================");
        System.out.println("Total rounds played: " + currentRound);
        System.out.println("\nFinal Standings:");

        for (Player p : players) {
            System.out.println("  " + p.getName() + " (ID: " + p.getId() + "):");
            System.out.println("    Victory Points: " + p.getVictoryPoints());
            System.out.println("    Resources: " + p.getResources());
            System.out.println("    Settlements: " + p.getSettlementCount()
                + ", Cities: " + p.getCityCount()
                + ", Roads: " + p.getRoadCount());
        }
        System.out.println("==================================================");
    }
}
