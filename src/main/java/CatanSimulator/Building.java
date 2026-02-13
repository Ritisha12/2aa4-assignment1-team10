package CatanSimulator;

// abstract building class - settlements and cities extend this
public abstract class Building {
    private Player owner;

    public Building(Player owner) {
        this.owner = owner;
    }

    public abstract int getVictoryPoints();
    public abstract ResourceCost getCost();

    public Player getOwner() { return owner; }
}
