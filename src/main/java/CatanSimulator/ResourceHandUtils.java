package CatanSimulator;

/**
 * Utility for snapshotting and restoring a ResourceHand.
 * Used by command implementations to support undo().
 */
public class ResourceHandUtils {

    private ResourceHandUtils() {}

    /** Create a deep copy of a ResourceHand. */
    public static ResourceHand copy(ResourceHand source) {
        ResourceHand copy = new ResourceHand();
        copy.addResource(ResourceType.WOOD,  source.getWood());
        copy.addResource(ResourceType.BRICK, source.getBrick());
        copy.addResource(ResourceType.SHEEP, source.getSheep());
        copy.addResource(ResourceType.WHEAT, source.getWheat());
        copy.addResource(ResourceType.ORE,   source.getOre());
        return copy;
    }

    /** Overwrite target with all values from snapshot. */
    public static void restore(ResourceHand target, ResourceHand snapshot) {
        target.removeResource(ResourceType.WOOD,  target.getWood());
        target.removeResource(ResourceType.BRICK, target.getBrick());
        target.removeResource(ResourceType.SHEEP, target.getSheep());
        target.removeResource(ResourceType.WHEAT, target.getWheat());
        target.removeResource(ResourceType.ORE,   target.getOre());

        target.addResource(ResourceType.WOOD,  snapshot.getWood());
        target.addResource(ResourceType.BRICK, snapshot.getBrick());
        target.addResource(ResourceType.SHEEP, snapshot.getSheep());
        target.addResource(ResourceType.WHEAT, snapshot.getWheat());
        target.addResource(ResourceType.ORE,   snapshot.getOre());
    }
}
