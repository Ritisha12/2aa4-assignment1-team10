public abstract class Building {
    private int victoryPointValue;
    private Player owner;
    
    public Building(int victoryPointValue, Player owner) {
        this.victoryPointValue = victoryPointValue;
        this.owner = owner;
    }
    
    public abstract int getVictoryPoints();
    public abstract ResourceCost getCost();
    
    // Getters
    public int getVictoryPointValue() { 
        return victoryPointValue; 
    }
    
    public Player getOwner() { 
        return owner; 
    }
}
