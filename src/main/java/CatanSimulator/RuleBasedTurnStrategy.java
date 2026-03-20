package CatanSimulator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RuleBasedTurnStrategy implements AiTurnStrategy {
    @Override
    public boolean takeTurn(
        AIPlayer player,
        Board board,
        List<Player> players,
        ActionLogger logger,
        int round,
        Random random
    ) {
        if (player.hasMoreThanCards(7)) {
            return spendCards(player, board, logger, round, random);
        }

        Edge roadToConnect = findConnectingRoad(player, board);
        if (roadToConnect != null) {
            return tryBuildRoad(player, roadToConnect, logger, round);
        }

        Edge roadToDefend = findDefensiveRoad(player, board, players);
        if (roadToDefend != null) {
            return tryBuildRoad(player, roadToDefend, logger, round);
        }

        return chooseBestValueMove(player, board, logger, round, random, false);
    }

    private boolean spendCards(
        AIPlayer player,
        Board board,
        ActionLogger logger,
        int round,
        Random random
    ) {
        if (chooseBestValueMove(player, board, logger, round, random, true)) {
            return true;
        }

        ResourceType give = findBestTradeSource(player);
        if (give == null) return false;

        ResourceType receive = findLowestResource(player, give);
        if (receive == null) return false;

        return bankTrade(player, give, receive, logger, round);
    }

    private boolean chooseBestValueMove(
        AIPlayer player,
        Board board,
        ActionLogger logger,
        int round,
        Random random,
        boolean includeTradeOnly
    ) {
        Node cityNode = firstCityNode(player, board);
        Node settlementNode = firstSettlementNode(player, board);
        Edge roadEdge = firstRoadEdge(player, board);

        double cityValue = moveValue(player, new City(player).getCost(), 1.0);
        double settlementValue = moveValue(player, new Settlement(player).getCost(), 1.0);
        double roadValue = moveValue(player, new Road(player, roadEdge).getCost(), 0.8);
        double tradeValue = includeTradeOnly ? 0.5 : -1.0;

        if (cityNode == null) cityValue = -1.0;
        if (settlementNode == null) settlementValue = -1.0;
        if (roadEdge == null) roadValue = -1.0;
        if (includeTradeOnly && findBestTradeSource(player) == null) tradeValue = -1.0;

        double bestValue = Math.max(Math.max(cityValue, settlementValue), Math.max(roadValue, tradeValue));
        if (bestValue < 0) return false;

        List<String> bestMoves = new ArrayList<>();
        if (cityValue == bestValue) bestMoves.add("city");
        if (settlementValue == bestValue) bestMoves.add("settlement");
        if (roadValue == bestValue) bestMoves.add("road");
        if (tradeValue == bestValue) bestMoves.add("trade");

        String choice = bestMoves.get(random.nextInt(bestMoves.size()));
        if (choice.equals("city")) {
            return tryBuildCity(player, cityNode, logger, round);
        }
        if (choice.equals("settlement")) {
            return tryBuildSettlement(player, settlementNode, logger, round);
        }
        if (choice.equals("road")) {
            return tryBuildRoad(player, roadEdge, logger, round);
        }

        ResourceType give = findBestTradeSource(player);
        ResourceType receive = findLowestResource(player, give);
        return bankTrade(player, give, receive, logger, round);
    }

    private double moveValue(AIPlayer player, ResourceCost cost, double baseValue) {
        if (cost == null) return -1.0;

        if (cost.canAfford(player.getResources())) {
            int cardsLeft = player.getResources().getTotalCards() - totalCost(cost);
            if (cardsLeft < 5) {
                return baseValue + 0.5;
            }
            return baseValue;
        }

        ResourceType missing = findSingleMissingResource(player, cost);
        ResourceType give = findTradeSource(player, missing);
        if (missing == null || give == null) return -1.0;

        int cardsLeft = player.getResources().getTotalCards() - totalCost(cost) - 3;
        if (cardsLeft < 5) {
            return baseValue + 0.5;
        }
        return baseValue;
    }

    private Node firstCityNode(AIPlayer player, Board board) {
        for (Node node : board.getNodes()) {
            if (node.canUpgradeToCity(player)) return node;
        }
        return null;
    }

    private Node firstSettlementNode(AIPlayer player, Board board) {
        for (Node node : board.getNodes()) {
            if (node.canBuildSettlement(player)) return node;
        }
        return null;
    }

    private Edge firstRoadEdge(AIPlayer player, Board board) {
        for (Edge edge : board.getEdges()) {
            if (edge.canBuildRoad(player)) return edge;
        }
        return null;
    }

    private boolean tryBuildCity(AIPlayer player, Node node, ActionLogger logger, int round) {
        if (node == null) return false;
        if (player.buildCityAt(node, logger, round)) return true;

        ResourceCost cost = new City(player).getCost();
        ResourceType missing = findSingleMissingResource(player, cost);
        ResourceType give = findTradeSource(player, missing);
        if (!bankTrade(player, give, missing, logger, round)) return false;
        return player.buildCityAt(node, logger, round);
    }

    private boolean tryBuildSettlement(AIPlayer player, Node node, ActionLogger logger, int round) {
        if (node == null) return false;
        if (player.buildSettlementAt(node, logger, round)) return true;

        ResourceCost cost = new Settlement(player).getCost();
        ResourceType missing = findSingleMissingResource(player, cost);
        ResourceType give = findTradeSource(player, missing);
        if (!bankTrade(player, give, missing, logger, round)) return false;
        return player.buildSettlementAt(node, logger, round);
    }

    private boolean tryBuildRoad(AIPlayer player, Edge edge, ActionLogger logger, int round) {
        if (edge == null) return false;
        if (player.buildRoadAt(edge, logger, round)) return true;

        ResourceCost cost = new Road(player, edge).getCost();
        ResourceType missing = findSingleMissingResource(player, cost);
        ResourceType give = findTradeSource(player, missing);
        if (!bankTrade(player, give, missing, logger, round)) return false;
        return player.buildRoadAt(edge, logger, round);
    }

    private boolean bankTrade(
        AIPlayer player,
        ResourceType give,
        ResourceType receive,
        ActionLogger logger,
        int round
    ) {
        if (give == null || receive == null) return false;
        if (!player.getResources().tradeFourToOne(give, receive)) return false;
        logger.logAction(round, player, "Traded 4 " + give + " for 1 " + receive);
        return true;
    }

    private ResourceType findSingleMissingResource(AIPlayer player, ResourceCost cost) {
        ResourceType missing = null;
        missing = updateMissingResource(player, missing, ResourceType.WOOD, cost.getWood());
        if (missing == ResourceType.DESERT) return null;
        missing = updateMissingResource(player, missing, ResourceType.BRICK, cost.getBrick());
        if (missing == ResourceType.DESERT) return null;
        missing = updateMissingResource(player, missing, ResourceType.SHEEP, cost.getSheep());
        if (missing == ResourceType.DESERT) return null;
        missing = updateMissingResource(player, missing, ResourceType.WHEAT, cost.getWheat());
        if (missing == ResourceType.DESERT) return null;
        missing = updateMissingResource(player, missing, ResourceType.ORE, cost.getOre());
        if (missing == ResourceType.DESERT) return null;
        return missing;
    }

    private ResourceType updateMissingResource(
        AIPlayer player,
        ResourceType currentMissing,
        ResourceType type,
        int required
    ) {
        int deficit = required - player.getResources().getCount(type);
        if (deficit <= 0) return currentMissing;
        if (deficit > 1 || currentMissing != null) return ResourceType.DESERT;
        return type;
    }

    private ResourceType findTradeSource(AIPlayer player, ResourceType missing) {
        if (missing == null) return null;

        ResourceType best = null;
        int bestCount = 0;
        for (ResourceType type : List.of(
            ResourceType.WOOD, ResourceType.BRICK, ResourceType.SHEEP,
            ResourceType.WHEAT, ResourceType.ORE)) {
            if (type == missing) continue;
            int count = player.getResources().getCount(type);
            if (count >= 4 && count > bestCount) {
                best = type;
                bestCount = count;
            }
        }
        return best;
    }

    private ResourceType findBestTradeSource(AIPlayer player) {
        ResourceType best = null;
        int bestCount = 0;
        for (ResourceType type : List.of(
            ResourceType.WOOD, ResourceType.BRICK, ResourceType.SHEEP,
            ResourceType.WHEAT, ResourceType.ORE)) {
            int count = player.getResources().getCount(type);
            if (count >= 4 && count > bestCount) {
                best = type;
                bestCount = count;
            }
        }
        return best;
    }

    private ResourceType findLowestResource(AIPlayer player, ResourceType excluded) {
        if (excluded == null) return null;

        ResourceType best = null;
        int lowest = Integer.MAX_VALUE;
        for (ResourceType type : List.of(
            ResourceType.WOOD, ResourceType.BRICK, ResourceType.SHEEP,
            ResourceType.WHEAT, ResourceType.ORE)) {
            if (type == excluded) continue;
            int count = player.getResources().getCount(type);
            if (count < lowest) {
                lowest = count;
                best = type;
            }
        }
        return best;
    }

    private int totalCost(ResourceCost cost) {
        return cost.getWood() + cost.getBrick() + cost.getSheep()
            + cost.getWheat() + cost.getOre();
    }

    private Edge findConnectingRoad(AIPlayer player, Board board) {
        Map<Edge, Integer> components = roadComponents(player, board);
        Set<Integer> componentIds = new HashSet<>(components.values());
        if (componentIds.size() < 2) return null;

        for (Edge edge : board.getEdges()) {
            if (!edge.canBuildRoad(player)) continue;

            Set<Integer> touching = touchingComponents(edge, components, player);
            if (touching.size() >= 2) return edge;

            if (touching.size() == 1) {
                int firstComponent = touching.iterator().next();
                for (Node node : List.of(edge.getNode1(), edge.getNode2())) {
                    for (Edge next : node.getAdjacentEdges()) {
                        if (next == edge || next.getRoad() != null) continue;
                        int otherComponent = componentBeyond(node, next, components, player);
                        if (otherComponent != -1 && otherComponent != firstComponent) {
                            return edge;
                        }
                    }
                }
            }
        }

        return null;
    }

    private Edge findDefensiveRoad(AIPlayer player, Board board, List<Player> players) {
        int myLongestRoad = longestRoad(player, board);
        if (myLongestRoad == 0) return null;

        int otherLongestRoad = 0;
        for (Player other : players) {
            if (other.equals(player)) continue;
            int roadLength = longestRoad(other, board);
            if (roadLength > otherLongestRoad) {
                otherLongestRoad = roadLength;
            }
        }

        if (otherLongestRoad < myLongestRoad - 1) return null;

        Edge bestEdge = null;
        int bestLength = myLongestRoad;

        for (Edge edge : board.getEdges()) {
            if (!edge.canBuildRoad(player)) continue;

            Road fakeRoad = new Road(player, edge);
            edge.placeRoad(fakeRoad);
            int newLength = longestRoad(player, board);
            edge.removeRoad();

            if (newLength > bestLength) {
                bestLength = newLength;
                bestEdge = edge;
            }
        }

        return bestEdge;
    }

    private int longestRoad(Player player, Board board) {
        List<Edge> playerRoads = new ArrayList<>();
        for (Edge edge : board.getEdges()) {
            if (edge.getRoad() != null && edge.getRoad().getOwner().equals(player)) {
                playerRoads.add(edge);
            }
        }

        int best = 0;
        for (Edge edge : playerRoads) {
            int current = longestRoadFrom(edge, player, new HashSet<>());
            if (current > best) best = current;
        }
        return best;
    }

    private int longestRoadFrom(Edge edge, Player player, Set<Edge> visited) {
        visited.add(edge);
        int best = 1;

        for (Node node : List.of(edge.getNode1(), edge.getNode2())) {
            for (Edge next : node.getAdjacentEdges()) {
                if (next == edge || visited.contains(next)) continue;
                if (next.getRoad() != null && next.getRoad().getOwner().equals(player)) {
                    int length = 1 + longestRoadFrom(next, player, visited);
                    if (length > best) best = length;
                }
            }
        }

        visited.remove(edge);
        return best;
    }

    private Map<Edge, Integer> roadComponents(AIPlayer player, Board board) {
        Map<Edge, Integer> components = new HashMap<>();
        int currentId = 0;

        for (Edge edge : board.getEdges()) {
            if (edge.getRoad() == null || !edge.getRoad().getOwner().equals(player)) continue;
            if (components.containsKey(edge)) continue;

            Deque<Edge> stack = new ArrayDeque<>();
            stack.push(edge);

            while (!stack.isEmpty()) {
                Edge current = stack.pop();
                if (components.containsKey(current)) continue;

                components.put(current, currentId);
                for (Node node : List.of(current.getNode1(), current.getNode2())) {
                    for (Edge next : node.getAdjacentEdges()) {
                        if (next.getRoad() != null
                            && next.getRoad().getOwner().equals(player)
                            && !components.containsKey(next)) {
                            stack.push(next);
                        }
                    }
                }
            }

            currentId++;
        }

        return components;
    }

    private Set<Integer> touchingComponents(
        Edge edge,
        Map<Edge, Integer> components,
        AIPlayer player
    ) {
        Set<Integer> result = new HashSet<>();

        for (Node node : List.of(edge.getNode1(), edge.getNode2())) {
            for (Edge next : node.getAdjacentEdges()) {
                if (next == edge || next.getRoad() == null) continue;
                if (!next.getRoad().getOwner().equals(player)) continue;

                Integer id = components.get(next);
                if (id != null) result.add(id);
            }
        }

        return result;
    }

    private int componentBeyond(
        Node fromNode,
        Edge emptyEdge,
        Map<Edge, Integer> components,
        AIPlayer player
    ) {
        Node otherNode;
        if (emptyEdge.getNode1().equals(fromNode)) {
            otherNode = emptyEdge.getNode2();
        } else {
            otherNode = emptyEdge.getNode1();
        }

        for (Edge next : otherNode.getAdjacentEdges()) {
            if (next == emptyEdge || next.getRoad() == null) continue;
            if (!next.getRoad().getOwner().equals(player)) continue;

            Integer id = components.get(next);
            if (id != null) return id;
        }

        return -1;
    }
}
