import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private int id;
    private String name;
    private int victoryPoints;
    private ResourceHand resources;
    private List<Road> roads;
    private List<Settlement> settlements;
    private List<City> cities;
    private Random random;
    
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.victoryPoints = 0;
        this.resources = new ResourceHand();
        this.roads = new ArrayList<>();
        this.settlements = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.random = new Random();
    }
    
    public int rollDice(Dice dice) {
        return dice.rollTwoDice();
    }
    
    public void collectResources(int diceRoll, Board board) {
        // For each tile with matching number token
        for (Tile tile : board.getTiles()) {
            if (tile.getNumberToken() == diceRoll && !tile.hasRobber()) {
                ResourceType resource = tile.produceResource();
                if (resource != null) {
                    // Check each adjacent node for this player's buildings
                    for (Node node : tile.getAdjacentNodes()) {
                        Building building = node.getBuilding();
                        if (building != null && building.getOwner().equals(this)) {
                            if (building instanceof Settlement) {
                                resources.addResource(resource, 1);
                            } else if (building instanceof City) {
                                resources.addResource(resource, 2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean takeTurn(Board board, ActionLogger logger, int round) {
        // Try to build something randomly
        List<String> possibleActions = new ArrayList<>();
        
        // Check what can be built
        if (canAffordSettlement()) {
            possibleActions.add("settlement");
        }
        if (canAffordCity()) {
            possibleActions.add("city");
        }
        if (canAffordRoad()) {
            possibleActions.add("road");
        }
        
        // Shuffle and try actions
        while (!possibleActions.isEmpty()) {
            int index = random.nextInt(possibleActions.size());
            String action = possibleActions.remove(index);
            
            boolean success = false;
            switch(action) {
                case "settlement":
                    success = tryBuildSettlement(board, logger, round);
                    break;
                case "city":
                    success = tryBuildCity(board, logger, round);
                    break;
                case "road":
                    success = tryBuildRoad(board, logger, round);
                    break;
            }
            
            if (success) {
                return true;
            }
        }
        
        logger.logAction(round, this, "No valid actions available");
        return false;
    }
    
    private boolean canAffordSettlement() {
        return new Settlement(this).getCost().canAfford(resources);
    }
    
    private boolean canAffordCity() {
        return new City(this).getCost().canAfford(resources);
    }
    
    private boolean canAffordRoad() {
        return Road.getCost().canAfford(resources);
    }
    
    private boolean tryBuildSettlement(Board board, ActionLogger logger, int round) {
        List<Node> validNodes = new ArrayList<>();
        
        for (Node node : board.getNodes()) {
            if (node.canBuildSettlement(this) && !node.isOccupied()) {
                // Additional check: must connect to existing road
                boolean hasAdjacentRoad = false;
                for (Edge edge : node.getAdjacentEdges()) {
                    if (edge.getRoad() != null && edge.getRoad().getOwner().equals(this)) {
                        hasAdjacentRoad = true;
                        break;
                    }
                }
                
                // For initial placements or if connected
                if (hasAdjacentRoad || roads.isEmpty()) {
                    validNodes.add(node);
                }
            }
        }
        
        if (!validNodes.isEmpty()) {
            Node chosen = validNodes.get(random.nextInt(validNodes.size()));
            return buildSettlement(chosen, board, logger, round);
        }
        
        return false;
    }
    
    private boolean tryBuildCity(Board board, ActionLogger logger, int round) {
        List<Node> validNodes = new ArrayList<>();
        
        for (Node node : board.getNodes()) {
            if (node.canUpgradeToCity(this)) {
                validNodes.add(node);
            }
        }
        
        if (!validNodes.isEmpty()) {
            Node chosen = validNodes.get(random.nextInt(validNodes.size()));
            return buildCity(chosen, board, logger, round);
        }
        
        return false;
    }
    
    private boolean tryBuildRoad(Board board, ActionLogger logger, int round) {
        List<Edge> validEdges = new ArrayList<>();
        
        for (Edge edge : board.getEdges()) {
            if (edge.canBuildRoad(this)) {
                validEdges.add(edge);
            }
        }
        
        if (!validEdges.isEmpty()) {
            Edge chosen = validEdges.get(random.nextInt(validEdges.size()));
            return buildRoad(chosen, board, logger, round);
        }
        
        return false;
    }
    
    public boolean buildSettlement(Node node, Board board, ActionLogger logger, int round) {
        Settlement settlement = new Settlement(this);
        if (settlement.getCost().canAfford(resources) && node.canBuildSettlement(this)) {
            settlement.getCost().deductFrom(resources);
            node.placeBuilding(settlement);
            settlements.add(settlement);
            victoryPoints += settlement.getVictoryPoints();
            logger.logAction(round, this, "Built settlement at node " + node.getId());
            return true;
        }
        return false;
    }
    
    public boolean buildCity(Node node, Board board, ActionLogger logger, int round) {
        City city = new City(this);
        if (city.getCost().canAfford(resources) && node.canUpgradeToCity(this)) {
            city.getCost().deductFrom(resources);
            victoryPoints -= 1; // Remove settlement VP
            node.upgradeToCity(city);
            cities.add(city);
            victoryPoints += city.getVictoryPoints();
            logger.logAction(round, this, "Upgraded to city at node " + node.getId());
            return true;
        }
        return false;
    }
    
    public boolean buildRoad(Edge edge, Board board, ActionLogger logger, int round) {
        if (Road.getCost().canAfford(resources) && edge.canBuildRoad(this)) {
            Road road = new Road(this, edge);
            Road.getCost().deductFrom(resources);
            edge.placeRoad(road);
            roads.add(road);
            logger.logAction(round, this, "Built road at edge " + edge.getId());
            return true;
        }
        return false;
    }
    
    public boolean hasMoreThanCards(int limit) {
        return resources.getTotalCards() > limit;
    }
    
    public boolean tryToBuild(Board board, ActionLogger logger, int round) {
        // R1.8: Try to spend cards when having more than 7
        return takeTurn(board, logger, round);
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public int getVictoryPoints() { 
        return victoryPoints; 
    }
    
    public ResourceHand getResources() { 
        return resources; 
    }
    
    public List<Road> getRoads() {
        return new ArrayList<>(roads);
    }
    
    @Override
    public String toString() {
        return String.format("Player[id=%d, name=%s, VP=%d, cards=%d]", 
                           id, name, victoryPoints, resources.getTotalCards());
    }
}
