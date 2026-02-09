public class City extends Building {
    
    public City(Player owner) {
        super(2, owner);
    }
    
    @Override
    public int getVictoryPoints() {
        return 2;
    }
    
    @Override
    public ResourceCost getCost() {
        // City costs: 2 wheat, 3 ore
        return new ResourceCost(0, 0, 0, 2, 3);
    }
    
    @Override
    public String toString() {
        return "City[owner=" + getOwner().getId() + "]";
    }
}
