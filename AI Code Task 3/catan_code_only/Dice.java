import java.util.Random;

public class Dice {
    private Random random;
    
    public Dice() {
        this.random = new Random();
    }
    
    // For testing with specific seed
    public Dice(long seed) {
        this.random = new Random(seed);
    }
    
    public int roll() {
        return random.nextInt(6) + 1; // Returns 1-6
    }
    
    public int rollSingleDie() {
        return roll();
    }
    
    // Roll two dice and return sum (standard Catan dice roll)
    public int rollTwoDice() {
        return roll() + roll();
    }
}
