package CatanSimulator;

import java.util.Random;

// tracks how many of each resource a player has
public class ResourceHand {
    private int wood;
    private int brick;
    private int sheep;
    private int wheat;
    private int ore;

    public ResourceHand() {
        wood = 0;
        brick = 0;
        sheep = 0;
        wheat = 0;
        ore = 0;
    }

    public void addResource(ResourceType type, int amount) {
        if (type == null || type == ResourceType.DESERT) return;
        switch(type) {
            case WOOD: wood += amount; break;
            case BRICK: brick += amount; break;
            case SHEEP: sheep += amount; break;
            case WHEAT: wheat += amount; break;
            case ORE: ore += amount; break;
            default: break;
        }
    }

    public boolean removeResource(ResourceType type, int amount) {
        switch(type) {
            case WOOD:
                if (wood >= amount) { wood -= amount; return true; }
                break;
            case BRICK:
                if (brick >= amount) { brick -= amount; return true; }
                break;
            case SHEEP:
                if (sheep >= amount) { sheep -= amount; return true; }
                break;
            case WHEAT:
                if (wheat >= amount) { wheat -= amount; return true; }
                break;
            case ORE:
                if (ore >= amount) { ore -= amount; return true; }
                break;
            default: break;
        }
        return false;
    }

    public int getTotalCards() {
        return wood + brick + sheep + wheat + ore;
    }

    public int getCount(ResourceType type) {
        switch (type) {
            case WOOD: return wood;
            case BRICK: return brick;
            case SHEEP: return sheep;
            case WHEAT: return wheat;
            case ORE: return ore;
            default: return 0;
        }
    }

    public boolean tradeFourToOne(ResourceType give, ResourceType receive) {
        if (give == null || receive == null || give == receive || receive == ResourceType.DESERT) {
            return false;
        }
        if (!removeResource(give, 4)) {
            return false;
        }
        addResource(receive, 1);
        return true;
    }

    public ResourceType removeRandomResource(Random random) {
        int totalCards = getTotalCards();
        if (totalCards == 0) {
            return null;
        }

        int chosenIndex = random.nextInt(totalCards) + 1;
        int runningTotal = wood;
        if (chosenIndex <= runningTotal) {
            wood--;
            return ResourceType.WOOD;
        }

        runningTotal += brick;
        if (chosenIndex <= runningTotal) {
            brick--;
            return ResourceType.BRICK;
        }

        runningTotal += sheep;
        if (chosenIndex <= runningTotal) {
            sheep--;
            return ResourceType.SHEEP;
        }

        runningTotal += wheat;
        if (chosenIndex <= runningTotal) {
            wheat--;
            return ResourceType.WHEAT;
        }

        ore--;
        return ResourceType.ORE;
    }

    public int getWood() { return wood; }
    public int getBrick() { return brick; }
    public int getSheep() { return sheep; }
    public int getWheat() { return wheat; }
    public int getOre() { return ore; }

    @Override
    public String toString() {
        return "Wood:" + wood + " Brick:" + brick + " Sheep:" + sheep
            + " Wheat:" + wheat + " Ore:" + ore;
    }
}
