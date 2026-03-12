package CatanSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Player {
    private int id;
    private String name;
    private PlayerColor color;
    private int victoryPoints;
    private ResourceHand resources;
    private List<Road> roads;
    private List<Settlement> settlements;
    private List<City> cities;
    private Random random;

    public Player(int id, String name) {
        this(id, name, PlayerColor.forPlayerId(id));
    }

    public Player(int id, String name, PlayerColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

        return false;
    }

    public boolean tryBankTradeForBuild(Board board, ActionLogger logger, int round) {
        if (tryBankTradeForSettlement(board, logger, round)) {
            return true;
        }
        if (tryBankTradeForCity(board, logger, round)) {
            return true;
        }
        return tryBankTradeForRoad(board, logger, round);
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
            return buildSettlementAt(chosen, logger, round);
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
            return buildCityAt(chosen, logger, round);
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
            return buildRoadAt(chosen, logger, round);
        }
        return false;
    }

    private boolean tryBankTradeForSettlement(Board board, ActionLogger logger, int round) {
        Node target = null;
        for (Node node : board.getNodes()) {
            if (node.canBuildSettlement(this)) {
                target = node;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!tradeForMissingSingleResource(new Settlement(this).getCost(), logger, round)) {
            return false;
        }
        return buildSettlementAt(target, logger, round);
    }

    private boolean tryBankTradeForCity(Board board, ActionLogger logger, int round) {
        Node target = null;
        for (Node node : board.getNodes()) {
            if (node.canUpgradeToCity(this)) {
                target = node;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!tradeForMissingSingleResource(new City(this).getCost(), logger, round)) {
            return false;
        }
        return buildCityAt(target, logger, round);
    }

    private boolean tryBankTradeForRoad(Board board, ActionLogger logger, int round) {
        Edge target = null;
        for (Edge edge : board.getEdges()) {
            if (edge.canBuildRoad(this)) {
                target = edge;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!tradeForMissingSingleResource(new Road(this, target).getCost(), logger, round)) {
            return false;
        }
        return buildRoadAt(target, logger, round);
    }

    private boolean tradeForMissingSingleResource(ResourceCost cost, ActionLogger logger, int round) {
        ResourceType missing = findSingleMissingResource(cost);
        if (missing == null) {
            return false;
        }

        ResourceType give = findTradeSource(missing);
        if (give == null || !resources.tradeFourToOne(give, missing)) {
            return false;
        }

        logger.logAction(round, this, "Traded 4 " + give + " for 1 " + missing);
        return true;
    }

    private ResourceType findSingleMissingResource(ResourceCost cost) {
        ResourceType missing = null;

        missing = updateMissingResource(missing, ResourceType.WOOD, cost.getWood());
        if (missing == ResourceType.DESERT) return null;

        missing = updateMissingResource(missing, ResourceType.BRICK, cost.getBrick());
        if (missing == ResourceType.DESERT) return null;

        missing = updateMissingResource(missing, ResourceType.SHEEP, cost.getSheep());
        if (missing == ResourceType.DESERT) return null;

        missing = updateMissingResource(missing, ResourceType.WHEAT, cost.getWheat());
        if (missing == ResourceType.DESERT) return null;

        missing = updateMissingResource(missing, ResourceType.ORE, cost.getOre());
        if (missing == ResourceType.DESERT) return null;

        return missing;
    }

    private ResourceType updateMissingResource(ResourceType currentMissing, ResourceType type, int required) {
        int deficit = required - resources.getCount(type);
        if (deficit <= 0) {
            return currentMissing;
        }
        if (deficit > 1 || currentMissing != null) {
            return ResourceType.DESERT;
        }
        return type;
    }

    private ResourceType findTradeSource(ResourceType missing) {
        ResourceType chosen = null;
        int bestCount = 0;

        for (ResourceType candidate : List.of(
            ResourceType.WOOD,
            ResourceType.BRICK,
            ResourceType.SHEEP,
            ResourceType.WHEAT,
            ResourceType.ORE
        )) {
            if (candidate == missing) {
                continue;
            }
            int count = resources.getCount(candidate);
            if (count >= 4 && count > bestCount) {
                chosen = candidate;
                bestCount = count;
            }
        }

        return chosen;
    }

    public boolean buildSettlementAt(Node node, ActionLogger logger, int round) {
        if (node == null) return false;
        Settlement s = new Settlement(this);
        if (!s.getCost().canAfford(resources) || !node.canBuildSettlement(this)) return false;
        if (!node.placeBuilding(s)) return false;

        s.getCost().deductFrom(resources);
        settlements.add(s);
        victoryPoints += s.getVictoryPoints();
        logger.logAction(round, this, "Built settlement at node " + node.getId());
        return true;
    }

    public boolean buildCityAt(Node node, ActionLogger logger, int round) {
        if (node == null) return false;
        City c = new City(this);
        if (!c.getCost().canAfford(resources) || !node.canUpgradeToCity(this)) return false;
        if (!node.upgradeToCity(c)) return false;

        c.getCost().deductFrom(resources);
        if (!settlements.isEmpty()) {
            settlements.remove(settlements.size() - 1);
        }
        cities.add(c);
        victoryPoints += 1; // net gain is +1 (was 1 for settlement, now 2 for city)
        logger.logAction(round, this, "Upgraded settlement to city at node " + node.getId());
        return true;
    }

    public boolean buildRoadAt(Edge edge, ActionLogger logger, int round) {
        if (edge == null) return false;
        Road r = new Road(this, edge);
        if (!r.getCost().canAfford(resources) || !edge.canBuildRoad(this)) return false;
        if (!edge.placeRoad(r)) return false;

        r.getCost().deductFrom(resources);
        roads.add(r);
        logger.logAction(round, this, "Built road at edge " + edge.getId());
        return true;
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

    public int discardRandomCards(int count, Random random) {
        int discarded = 0;
        for (int i = 0; i < count; i++) {
            if (resources.removeRandomResource(random) == null) {
                break;
            }
            discarded++;
        }
        return discarded;
    }

    public ResourceType stealRandomResource(Random random) {
        return resources.removeRandomResource(random);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public PlayerColor getColor() { return color; }
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
