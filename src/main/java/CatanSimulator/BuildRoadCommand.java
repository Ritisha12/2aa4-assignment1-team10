package CatanSimulator;

/**
 * Command Pattern - Concrete Command (R3.1: Undo/Redo)
 *
 * Encapsulates placing a road on a given edge.
 * On undo(), removes the road and restores spent resources.
 */
public class BuildRoadCommand implements GameCommand {

    private final Player player;
    private final Edge edge;
    private final ActionLogger logger;
    private final int round;

    private ResourceHand previousResources;
    private Road placedRoad;

    public BuildRoadCommand(Player player, Edge edge, ActionLogger logger, int round) {
        this.player = player;
        this.edge = edge;
        this.logger = logger;
        this.round = round;
    }

    @Override
    public boolean execute() {
        previousResources = ResourceHandUtils.copy(player.getResources());

        Road r = new Road(player, edge);
        if (!r.getCost().canAfford(player.getResources()) || !edge.canBuildRoad(player)) {
            return false;
        }
        if (!edge.placeRoad(r)) {
            return false;
        }

        r.getCost().deductFrom(player.getResources());
        player.addRoad(r);
        logger.logAction(round, player, "Built road at edge " + edge.getId());

        placedRoad = r;
        return true;
    }

    @Override
    public void undo() {
        if (placedRoad == null) return;

        edge.removeRoad();
        ResourceHandUtils.restore(player.getResources(), previousResources);
        player.removeRoad(placedRoad);

        logger.logAction(round, player, "UNDO: Removed road at edge " + edge.getId());
        placedRoad = null;
    }
}
