package CatanSimulator;

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

    // collect resources from tiles that match the dice roll
    public void collectResources(int diceRoll, Board board) {
        for (Tile tile : board.getTiles()) {
            if (tile.getNumberToken() == diceRoll) {
                ResourceType res = tile.produceResource();
                if (res != null) {
                    for (Node node : tile.getAdjacentNodes()) {
                        Building b = node.getBuilding();
                        if (b != null && b.getOwner().equals(this)) {
                            int amt = (b instanceof City) ? 2 : 1;
                            resources.addResource(res, amt);
                        }
                    }
                }
            }
        }
    }

    // try to build something randomly from what we can afford
    public boolean takeTurn(Board board, ActionLogger logger, int round) {
        List<String> options = new ArrayList<>();
        if (canAffordSettlement()) options.add("settlement");
        if (canAffordCity()) options.add("city");
        if (canAffordRoad()) options.add("road");

        while (!options.isEmpty()) {
            int idx = random.nextInt(options.size());
            String action = options.remove(idx);
            boolean ok = false;
            switch (action) {
                case "settlement": ok = tryBuildSettlement(board, logger, round); break;
                case "city": ok = tryBuildCity(board, logger, round); break;
                case "road": ok = tryBuildRoad(board, logger, round); break;
            }
            if (ok) return true;
        }

        logger.logAction(round, this, "No valid actions available");
        return false;
    }

    // used when player has >7 cards and needs to spend
    public boolean tryToBuild(Board board, ActionLogger logger, int round) {
        return takeTurn(board, logger, round);
    }

    private boolean canAffordSettlement() {
        return new Settlement(this).getCost().canAfford(resources);
    }
    private boolean canAffordCity() {
        return new City(this).getCost().canAfford(resources);
    }
    private boolean canAffordRoad() {
        // road costs 1 wood 1 brick
        return resources.getWood() >= 1 && resources.getBrick() >= 1;
    }

    private boolean tryBuildSettlement(Board board, ActionLogger logger, int round) {
        List<Node> valid = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (n.canBuildSettlement(this)) valid.add(n);
        }
        if (!valid.isEmpty()) {
            Node chosen = valid.get(random.nextInt(valid.size()));
            return buildSettlement(chosen, logger, round);
        }
        return false;
    }

    private boolean tryBuildCity(Board board, ActionLogger logger, int round) {
        List<Node> valid = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (n.canUpgradeToCity(this)) valid.add(n);
        }
        if (!valid.isEmpty()) {
            Node chosen = valid.get(random.nextInt(valid.size()));
            return buildCity(chosen, logger, round);
        }
        return false;
    }

    private boolean tryBuildRoad(Board board, ActionLogger logger, int round) {
        List<Edge> valid = new ArrayList<>();
        for (Edge e : board.getEdges()) {
            if (e.canBuildRoad(this)) valid.add(e);
        }
        if (!valid.isEmpty()) {
            Edge chosen = valid.get(random.nextInt(valid.size()));
            return buildRoad(chosen, logger, round);
        }
        return false;
    }

    private boolean buildSettlement(Node node, ActionLogger logger, int round) {
        Settlement s = new Settlement(this);
        if (s.getCost().canAfford(resources)) {
            s.getCost().deductFrom(resources);
            node.placeBuilding(s);
            settlements.add(s);
            victoryPoints += s.getVictoryPoints();
            logger.logAction(round, this, "Built settlement at node " + node.getId());
            return true;
        }
        return false;
    }

    private boolean buildCity(Node node, ActionLogger logger, int round) {
        City c = new City(this);
        if (c.getCost().canAfford(resources)) {
            c.getCost().deductFrom(resources);
            node.upgradeToCity(c);
            cities.add(c);
            victoryPoints += 1; // net gain is +1 (was 1 for settlement, now 2 for city)
            logger.logAction(round, this, "Upgraded settlement to city at node " + node.getId());
            return true;
        }
        return false;
    }

    private boolean buildRoad(Edge edge, ActionLogger logger, int round) {
        Road r = new Road(this, edge);
        if (r.getCost().canAfford(resources)) {
            r.getCost().deductFrom(resources);
            edge.placeRoad(r);
            roads.add(r);
            logger.logAction(round, this, "Built road at edge " + edge.getId());
            return true;
        }
        return false;
    }

    // initial placement stuff (free, no road connectivity needed)

    public void placeInitialSettlement(Node node) {
        Settlement s = new Settlement(this);
        node.placeBuilding(s);
        settlements.add(s);
        victoryPoints += 1;
    }

    public void placeInitialRoad(Edge edge) {
        Road r = new Road(this, edge);
        edge.placeRoad(r);
        roads.add(r);
    }

    // give starting resources from tiles adjacent to second settlement
    public void collectStartingResources(Node settlementNode) {
        for (Tile tile : settlementNode.getAdjacentTiles()) {
            ResourceType res = tile.getResourceType();
            if (res != ResourceType.DESERT) {
                resources.addResource(res, 1);
            }
        }
    }

    public boolean hasMoreThanCards(int limit) {
        return resources.getTotalCards() > limit;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getVictoryPoints() { return victoryPoints; }
    public ResourceHand getResources() { return resources; }
    public int getRoadCount() { return roads.size(); }
    public int getSettlementCount() { return settlements.size(); }
    public int getCityCount() { return cities.size(); }

    @Override
    public String toString() {
        return "Player[" + id + ", " + name + ", VP=" + victoryPoints + ", cards=" + resources.getTotalCards() + "]";
    }
}
