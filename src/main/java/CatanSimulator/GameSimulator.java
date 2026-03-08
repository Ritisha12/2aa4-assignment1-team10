package CatanSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// main game simulation class
// handles the game loop, initial placements, win conditions, etc
public class GameSimulator {
    private int currentRound;
    private int currentTurnId;
    private int maxRounds;
    private Board board;
    private List<Player> players;
    private Dice dice;
    private ActionLogger logger;
    private Random random;
    private BufferedReader input;
    private PrintStream output;
    private GameStateExporter exporter;

    public GameSimulator() {
        this(new BufferedReader(new InputStreamReader(System.in)), System.out);
    }

    public GameSimulator(BufferedReader input, PrintStream output) {
        currentRound = 0;
        currentTurnId = 0;
        maxRounds = 100;
        board = new Board();
        players = new ArrayList<>();
        dice = new Dice();
        logger = new ActionLogger();
        random = new Random();
        this.input = input;
        this.output = output;
        exporter = new GameStateExporter(Path.of("target", "visualizer", "state.json"));

        players.add(new HumanPlayer(0, "Player0", PlayerColor.RED, input, output));
        players.add(new AIPlayer(1, "Player1", PlayerColor.BLUE));
        players.add(new AIPlayer(2, "Player2", PlayerColor.ORANGE));
        players.add(new AIPlayer(3, "Player3", PlayerColor.WHITE));
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
                waitForGo();
                currentTurnId++;

                if (player instanceof HumanPlayer) {
                    runHumanTurn((HumanPlayer) player);
                } else {
                    runAiTurn(player);
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
        exporter.export(board);

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
            exporter.export(board);
        }
    }

    private void runAiTurn(Player player) {
        int diceRoll = player.rollDice(dice);
        handleDiceRoll(player, diceRoll);

        boolean built = player.takeTurn(board, logger, currentTurnId);
        if (!built) {
            built = player.tryBankTradeForBuild(board, logger, currentTurnId);
        }

        if (built) {
            exporter.export(board);
        } else {
            logger.logAction(currentTurnId, player, "No valid actions available");
        }
    }

    private void runHumanTurn(HumanPlayer player) {
        boolean rolled = false;

        while (true) {
            Command command = player.readCommand();

            switch (command.getType()) {
                case INVALID:
                    player.printMessage("Invalid command.");
                    break;
                case LIST:
                    player.printHand();
                    break;
                case ROLL:
                    if (rolled) {
                        player.printMessage("You already rolled.");
                        break;
                    }
                    handleDiceRoll(player, player.rollDice(dice));
                    rolled = true;
                    break;
                case BUILD_SETTLEMENT:
                    if (!rolled) {
                        player.printMessage("Roll first.");
                        break;
                    }
                    if (player.buildSettlementAt(board.getNodeById(command.getFirstId()), logger, currentTurnId)) {
                        exporter.export(board);
                    } else {
                        player.printMessage("Cannot build settlement there.");
                    }
                    break;
                case BUILD_CITY:
                    if (!rolled) {
                        player.printMessage("Roll first.");
                        break;
                    }
                    if (player.buildCityAt(board.getNodeById(command.getFirstId()), logger, currentTurnId)) {
                        exporter.export(board);
                    } else {
                        player.printMessage("Cannot build city there.");
                    }
                    break;
                case BUILD_ROAD:
                    if (!rolled) {
                        player.printMessage("Roll first.");
                        break;
                    }
                    Edge edge = board.getEdgeByNodeIds(command.getFirstId(), command.getSecondId());
                    if (player.buildRoadAt(edge, logger, currentTurnId)) {
                        exporter.export(board);
                    } else {
                        player.printMessage("Cannot build road there.");
                    }
                    break;
                case GO:
                    if (!rolled) {
                        player.printMessage("Roll first.");
                        break;
                    }
                    return;
                default:
                    player.printMessage("Invalid command.");
                    break;
            }
        }
    }

    private void handleDiceRoll(Player player, int diceRoll) {
        logger.logAction(currentTurnId, player, "Rolled " + diceRoll);

        if (diceRoll == 7) {
            logger.logAction(currentTurnId, player, "Rolled 7 - no resources produced");
            handleRobber(player);
            return;
        }

        for (Player p : players) {
            int before = p.getResources().getTotalCards();
            p.collectResources(diceRoll, board);
            int after = p.getResources().getTotalCards();
            if (after > before) {
                logger.logAction(currentTurnId, p,
                    "Collected " + (after - before) + " resources from roll " + diceRoll);
            }
        }
    }

    private void handleRobber(Player rollingPlayer) {
        for (Player player : players) {
            if (!player.hasMoreThanCards(7)) {
                continue;
            }

            int discardCount = player.getResources().getTotalCards() / 2;
            int discarded = player.discardRandomCards(discardCount, random);
            logger.logAction(currentTurnId, player, "Discarded " + discarded + " cards due to robber");
        }

        Tile currentRobberTile = board.getRobberTile();
        List<Tile> destinations = new ArrayList<>();
        for (Tile tile : board.getTiles()) {
            if (tile != currentRobberTile) {
                destinations.add(tile);
            }
        }

        if (destinations.isEmpty()) {
            return;
        }

        Tile destination = destinations.get(random.nextInt(destinations.size()));
        board.moveRobber(destination);
        logger.logAction(currentTurnId, rollingPlayer, "Moved robber to tile " + destination.getId());
        exporter.export(board);

        Player victim = findRobberVictim(destination, rollingPlayer);
        if (victim == null) {
            return;
        }

        ResourceType stolenResource = victim.stealRandomResource(random);
        if (stolenResource == null) {
            return;
        }

        rollingPlayer.getResources().addResource(stolenResource, 1);
        logger.logAction(currentTurnId, rollingPlayer,
            "Stole 1 " + stolenResource + " from " + victim.getName());
    }

    private Player findRobberVictim(Tile robberTile, Player rollingPlayer) {
        Set<Player> candidates = new LinkedHashSet<>();

        for (Node node : robberTile.getAdjacentNodes()) {
            Building building = node.getBuilding();
            if (building == null) {
                continue;
            }

            Player owner = building.getOwner();
            if (owner.equals(rollingPlayer)) {
                continue;
            }
            if (owner.getResources().getTotalCards() == 0) {
                continue;
            }
            candidates.add(owner);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        List<Player> candidateList = new ArrayList<>(candidates);
        return candidateList.get(random.nextInt(candidateList.size()));
    }

    private void waitForGo() {
        output.print("advance> ");
        output.flush();

        while (true) {
            String line = readLine();
            if ("go".equalsIgnoreCase(line.trim())) {
                return;
            }
            output.println("Type 'go' to continue.");
            output.print("advance> ");
            output.flush();
        }
    }

    private String readLine() {
        try {
            String line = input.readLine();
            if (line == null) {
                throw new IllegalStateException("Input stream closed.");
            }
            return line;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read step-forward command.", e);
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
