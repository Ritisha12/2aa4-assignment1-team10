package CatanSimulator;

/**
 * Command Pattern - Concrete Command (R3.1: Undo/Redo)
 *
 * Encapsulates building a settlement at a given node.
 * Stores a resource snapshot before execution so undo() can fully restore state.
 */
public class BuildSettlementCommand implements GameCommand {

    private final Player player;
    private final Node node;
    private final ActionLogger logger;
    private final int round;

    // saved before execute() — used to restore resources on undo()
    private ResourceHand previousResources;
    // reference to the placed settlement — used to remove it on undo()
    private Settlement placedSettlement;

    public BuildSettlementCommand(Player player, Node node, ActionLogger logger, int round) {
        this.player = player;
        this.node = node;
        this.logger = logger;
        this.round = round;
    }

    @Override
    public boolean execute() {
        previousResources = ResourceHandUtils.copy(player.getResources());

        Settlement s = new Settlement(player);
        if (!s.getCost().canAfford(player.getResources()) || !node.canBuildSettlement(player)) {
            return false;
        }
        if (!node.placeBuilding(s)) {
            return false;
        }

        s.getCost().deductFrom(player.getResources());
        player.addSettlement(s);
        player.addVictoryPoints(s.getVictoryPoints());
        logger.logAction(round, player, "Built settlement at node " + node.getId());

        placedSettlement = s;
        return true;
    }

    @Override
    public void undo() {
        if (placedSettlement == null) return;

        node.removeBuilding();
        ResourceHandUtils.restore(player.getResources(), previousResources);
        player.removeSettlement(placedSettlement);
        player.addVictoryPoints(-placedSettlement.getVictoryPoints());

        logger.logAction(round, player, "UNDO: Removed settlement at node " + node.getId());
        placedSettlement = null;
    }
}
