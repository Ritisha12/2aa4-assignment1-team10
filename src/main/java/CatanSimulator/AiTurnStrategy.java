package CatanSimulator;

import java.util.List;
import java.util.Random;

public interface AiTurnStrategy {
    boolean takeTurn(
        AIPlayer player,
        Board board,
        List<Player> players,
        ActionLogger logger,
        int round,
        Random random
    );
}
