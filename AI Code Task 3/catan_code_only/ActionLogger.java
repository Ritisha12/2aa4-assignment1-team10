import java.util.List;

public class ActionLogger {
    
    /**
     * Logs an action in the format: [RoundNumber] / [PlayerID]: [Action]
     * @param round The current round number
     * @param player The player performing the action
     * @param action Description of the action
     */
    public void logAction(int round, Player player, String action) {
        System.out.println("[" + round + "] / [" + player.getId() + "]: " + action);
    }
    
    /**
     * Logs victory points for all players at the end of a round
     * @param players List of all players
     */
    public void logVictoryPoints(List<Player> players) {
        System.out.println("--- Victory Points ---");
        for (Player player : players) {
            System.out.println("Player " + player.getId() + " (" + player.getName() + "): " + 
                             player.getVictoryPoints() + " VP");
        }
        System.out.println("----------------------");
    }
    
    /**
     * Logs the resources collected by a player
     * @param round Current round
     * @param player The player
     * @param resource Type of resource
     * @param amount Amount collected
     */
    public void logResourceCollection(int round, Player player, ResourceType resource, int amount) {
        if (amount > 0) {
            System.out.println("[" + round + "] / [" + player.getId() + "]: Collected " + 
                             amount + " " + resource);
        }
    }
}
