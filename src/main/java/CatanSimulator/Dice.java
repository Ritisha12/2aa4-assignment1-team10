package CatanSimulator;

import java.security.SecureRandom;
import java.util.Random;

// simulates rolling two dice
public class Dice {
    private Random random;

    public Dice() {
        random = new SecureRandom();
    }

    public int rollTwoDice() {
        int d1 = random.nextInt(6) + 1;
        int d2 = random.nextInt(6) + 1;
        return d1 + d2;
    }
}
