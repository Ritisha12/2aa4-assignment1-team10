package CatanSimulator;

/**
 * Command Pattern - Concrete Command (R3.1: Undo/Redo)
 *
 * Encapsulates upgrading a settlement to a city.
 * On undo(), the city is downgraded back to the original settlement.
 */
public class BuildCityCommand implements GameCommand {

    private final Player player;
    private final Node node;
    private final ActionLogger logger;
    private final int round;

    private ResourceHand previousResources;
    private City placedCity;
    // the settlement that was replaced — restored on undo()
    private Settlement replacedSettlement;

    public BuildCityCommand(Player player, Node node, ActionLogger logger, int round) {
        this.player = player;
        this.node = node;
        this.logger = logger;
        this.round = round;
    }

    @Override
    public boolean execute() {
        previousResources = ResourceHandUtils.copy(player.getResources());

        City c = new City(player);
        if (!c.getCost().canAfford(player.getResources()) || !node.canUpgradeToCity(player)) {
            return false;
        }
        if (!node.upgradeToCity(c)) {
            return false;
        }

        c.getCost().deductFrom(player.getResources());

        // remove the settlement that was just replaced
        replacedSettlement = player.removeLastSettlement();
        player.addCity(c);
        player.addVictoryPoints(1); // net +1: settlement was 1 VP, city is 2 VP

        logger.logAction(round, player, "Upgraded settlement to city at node " + node.getId());
        placedCity = c;
        return true;
    }

    @Override
    public void undo() {
        if (placedCity == null) return;

        // put settlement back on the node
        Settlement s = (replacedSettlement != null) ? replacedSettlement : new Settlement(player);
        node.downgradeCityToSettlement(s);

        ResourceHandUtils.restore(player.getResources(), previousResources);
        player.removeCity(placedCity);
        player.addSettlement(s);
        player.addVictoryPoints(-1);

        logger.logAction(round, player, "UNDO: Reverted city to settlement at node " + node.getId());
        placedCity = null;
    }
}
