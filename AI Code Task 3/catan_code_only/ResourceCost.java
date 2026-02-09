import java.util.HashMap;
import java.util.Map;

public class ResourceCost {
    private int wood;
    private int brick;
    private int sheep;
    private int wheat;
    private int ore;
    
    public ResourceCost(int wood, int brick, int sheep, int wheat, int ore) {
        this.wood = wood;
        this.brick = brick;
        this.sheep = sheep;
        this.wheat = wheat;
        this.ore = ore;
    }
    
    public boolean canAfford(ResourceHand hand) {
        return hand.getWood() >= wood &&
               hand.getBrick() >= brick &&
               hand.getSheep() >= sheep &&
               hand.getWheat() >= wheat &&
               hand.getOre() >= ore;
    }
    
    public void deductFrom(ResourceHand hand) {
        if (canAfford(hand)) {
            hand.removeResource(ResourceType.WOOD, wood);
            hand.removeResource(ResourceType.BRICK, brick);
            hand.removeResource(ResourceType.SHEEP, sheep);
            hand.removeResource(ResourceType.WHEAT, wheat);
            hand.removeResource(ResourceType.ORE, ore);
        }
    }
    
    // Getters
    public int getWood() { return wood; }
    public int getBrick() { return brick; }
    public int getSheep() { return sheep; }
    public int getWheat() { return wheat; }
    public int getOre() { return ore; }
}
