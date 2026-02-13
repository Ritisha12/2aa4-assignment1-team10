package CatanSimulator;

import java.util.List;

// handles logging game actions to console
public class ActionLogger {

    public void logAction(int round, Player player, String action) {
        System.out.println("[" + round + "] / [" + player.getId() + "]: " + action);
    }

    public void logVictoryPoints(int round, List<Player> players) {
        System.out.println("--- Victory Points (end of round " + round + ") ---");
        for (Player p : players) {
            System.out.println("  Player " + p.getId() + " (" + p.getName() + "): " + p.getVictoryPoints() + " VP");
        }
        System.out.println("---------------------------------------------");
    }
}
