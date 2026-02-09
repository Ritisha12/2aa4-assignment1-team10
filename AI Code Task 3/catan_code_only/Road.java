public class Road {
    private Player owner;
    private Edge location;
    
    public Road(Player owner, Edge location) {
        this.owner = owner;
        this.location = location;
    }
    
    public static ResourceCost getCost() {
        // Road costs: 1 wood, 1 brick
        return new ResourceCost(1, 1, 0, 0, 0);
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public Edge getLocation() {
        return location;
    }
    
    @Override
    public String toString() {
        return "Road[owner=" + owner.getId() + "]";
    }
}
