package CatanSimulator;

public class Robber {
    private Tile currentTile;

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void placeOn(Tile destination) {
        if (destination == null || destination == currentTile) {
            return;
        }
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        currentTile = destination;
        currentTile.setRobber(true);
    }
}
