public class Settlement extends Building {
    
    public Settlement(Player owner) {
        super(1, owner);
    }
    
    @Override
    public int getVictoryPoints() {
        return 1;
    }
    
    @Override
    public ResourceCost getCost() {
        // Settlement costs: 1 wood, 1 brick, 1 sheep, 1 wheat
        return new ResourceCost(1, 1, 1, 1, 0);
    }
    
    @Override
    public String toString() {
        return "Settlement[owner=" + getOwner().getId() + "]";
    }
}
