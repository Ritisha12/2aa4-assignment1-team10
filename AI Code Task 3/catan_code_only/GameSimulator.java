import java.util.ArrayList;
import java.util.List;

public class GameSimulator {
    private int currentRound;
    private int maxRounds;
    private Board board;
    private List<Player> players;
    private Dice dice;
    private ActionLogger logger;
    
    public GameSimulator() {
        this.currentRound = 0;
        this.maxRounds = 100; // Default
        this.board = new Board();
        this.players = new ArrayList<>();
        this.dice = new Dice();
        this.logger = new ActionLogger();
        
        // Initialize 4 players as per R1.2
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i, "Player" + i));
        }
    }
    
    /**
     * Check if any player has reached 10 victory points (R1.4)
     */
    public boolean checkWinCondition() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= 10) {
                System.out.println("\n*** WINNER: " + player.getName() + " with " + 
                                 player.getVictoryPoints() + " victory points! ***\n");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Run the simulation (R1.2, R1.3, R1.4, R1.5)
     */
    public void runSimulation() {
        // R1.1: Initialize board
        board.initializeBoard();
        System.out.println("Board initialized with " + board.getTiles().size() + " tiles, " +
                         board.getNodes().size() + " nodes, and " + 
                         board.getEdges().size() + " edges.\n");
        
        // Initial placements for each player (simplified)
        performInitialPlacements();
        
        // Main game loop
        while (currentRound < maxRounds && !checkWinCondition()) {
            currentRound++;
            System.out.println("\n=== Round " + currentRound + " ===");
            
            // Each player takes a turn
            for (Player player : players) {
                // Roll dice
                int diceRoll = player.rollDice(dice);
                logger.logAction(currentRound, player, "Rolled " + diceRoll);
                
                // R1.3: Handle dice roll
                if (diceRoll == 7) {
                    // Per assignment: ignore robber, just continue
                    logger.logAction(currentRound, player, "Rolled 7 (no resources produced)");
                } else {
                    // Collect resources
                    int resourcesBefore = player.getResources().getTotalCards();
                    player.collectResources(diceRoll, board);
                    int resourcesAfter = player.getResources().getTotalCards();
                    int collected = resourcesAfter - resourcesBefore;
                    
                    if (collected > 0) {
                        logger.logAction(currentRound, player, 
                                       "Collected " + collected + " resources");
                    }
                }
                
                // Take turn (build actions)
                player.takeTurn(board, logger, currentRound);
                
                // R1.8: If more than 7 cards, try to build
                if (player.hasMoreThanCards(7)) {
                    logger.logAction(currentRound, player, 
                                   "Has " + player.getResources().getTotalCards() + 
                                   " cards (>7), attempting to build...");
                    player.tryToBuild(board, logger, currentRound);
                }
            }
            
            // R1.7: Print victory points at end of round
            logger.logVictoryPoints(players);
        }
        
        // R1.5: Print final game state
        printGameState();
    }
    
    /**
     * Perform initial settlement and road placements for all players
     */
    private void performInitialPlacements() {
        System.out.println("=== Initial Placements ===");
        
        // Each player places 2 settlements and 2 roads
        for (int placement = 0; placement < 2; placement++) {
            for (Player player : players) {
                // Find valid node for settlement
                Node settlementNode = findValidSettlementNode(player);
                if (settlementNode != null) {
                    Settlement settlement = new Settlement(player);
                    settlementNode.placeBuilding(settlement);
                    player.getResources().clear(); // Don't charge for initial placement
                    
                    // Give starting resources on second placement
                    if (placement == 1) {
                        for (Tile tile : settlementNode.getAdjacentTiles()) {
                            ResourceType resource = tile.getResourceType();
                            if (resource != ResourceType.DESERT) {
                                player.getResources().addResource(resource, 1);
                            }
                        }
                    }
                    
                    System.out.println("Player " + player.getId() + 
                                     " placed initial settlement at node " + 
                                     settlementNode.getId());
                    
                    // Place road adjacent to settlement
                    Edge roadEdge = findValidRoadEdge(player, settlementNode);
                    if (roadEdge != null) {
                        Road road = new Road(player, roadEdge);
                        roadEdge.placeRoad(road);
                        player.getRoads().add(road);
                        System.out.println("Player " + player.getId() + 
                                         " placed initial road at edge " + 
                                         roadEdge.getId());
                    }
                }
            }
        }
        
        // Update victory points for initial settlements
        for (Player player : players) {
            player.getResources().clear(); // Reset after initial setup
            // Count settlements on board
            int settlements = 0;
            for (Node node : board.getNodes()) {
                if (node.getBuilding() instanceof Settlement &&
                    node.getBuilding().getOwner().equals(player)) {
                    settlements++;
                }
            }
            // Note: Player class should track VP, but we'll rely on builds during game
        }
        
        System.out.println("=== Initial Placements Complete ===\n");
    }
    
    private Node findValidSettlementNode(Player player) {
        List<Node> validNodes = new ArrayList<>();
        
        for (Node node : board.getNodes()) {
            if (!node.isOccupied()) {
                boolean validDistance = true;
                for (Node adjacent : node.getAdjacentNodes()) {
                    if (adjacent.isOccupied()) {
                        validDistance = false;
                        break;
                    }
                }
                if (validDistance) {
                    validNodes.add(node);
                }
            }
        }
        
        if (!validNodes.isEmpty()) {
            return validNodes.get(0); // Take first valid node
        }
        return null;
    }
    
    private Edge findValidRoadEdge(Player player, Node settlement) {
        for (Edge edge : settlement.getAdjacentEdges()) {
            if (edge.getRoad() == null) {
                return edge;
            }
        }
        return null;
    }
    
    /**
     * Print final game state
     */
    public void printGameState() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== GAME ENDED ===");
        System.out.println("=".repeat(50));
        System.out.println("Total rounds played: " + currentRound);
        System.out.println("\nFinal Standings:");
        
        for (Player player : players) {
            System.out.println(player.getName() + " (ID: " + player.getId() + "):");
            System.out.println("  Victory Points: " + player.getVictoryPoints());
            System.out.println("  Resources: " + player.getResources());
            System.out.println("  Roads: " + player.getRoads().size());
        }
        
        System.out.println("=".repeat(50));
    }
    
    /**
     * Load configuration from file (R1.4)
     */
    public void loadConfig(String filename) {
        ConfigurationReader reader = new ConfigurationReader();
        this.maxRounds = reader.readConfig(filename);
        System.out.println("Loaded configuration: max rounds = " + maxRounds);
    }
    
    // Getters for testing
    public int getCurrentRound() {
        return currentRound;
    }
    
    public int getMaxRounds() {
        return maxRounds;
    }
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    public Board getBoard() {
        return board;
    }
}
