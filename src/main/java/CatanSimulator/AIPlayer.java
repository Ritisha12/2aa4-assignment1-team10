package CatanSimulator;

import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {
    private final AiTurnStrategy strategy;
    private final Random random;

    public AIPlayer(int id, String name, PlayerColor color) {
        this(id, name, color, new RuleBasedTurnStrategy());
    }

    public AIPlayer(int id, String name, PlayerColor color, AiTurnStrategy strategy) {
        super(id, name, color);
        this.strategy = strategy;
        this.random = new Random();
    }

    public boolean takeStrategicTurn(Board board, List<Player> players, ActionLogger logger, int round) {
        return strategy.takeTurn(this, board, players, logger, round, random);
    }
}
