package CatanSimulator;

// Demonstrator for the Settlers of Catan simulator.
// Running this will set up the board, create 4 players, and simulate a full game.
//
// Key things to observe:
// - Board is set up from MapSetup with 19 tiles, 54 nodes, and edges (R1.1)
// - 4 random agents that pick build actions randomly (R1.2)
// - Game follows catan rules: roll dice, collect resources, build (R1.3)
// - Game runs for configurable number of rounds or until 10 VP (R1.4, R1.5)
// - Distance rule, road connectivity, and city upgrades are enforced (R1.6)
// - Actions printed as [Round] / [PlayerID]: [Action], VP shown each round (R1.7)
// - Players with >7 cards try to build to spend them (R1.8)
public class Demonstrator {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  SETTLERS OF CATAN SIMULATOR");
        System.out.println("  SFWRENG 2AA4 - Assignment 1");
        System.out.println("============================================================");
        System.out.println();

        // creates the board, 4 players, dice, and logger
        GameSimulator sim = new GameSimulator();

        // R1.4: load config file for number of rounds (format: "turns: <int>")
        if (args.length > 0) {
            System.out.println("Loading config from: " + args[0]);
            sim.loadConfig(args[0]);
        } else {
            System.out.println("No config file provided, using default 100 rounds.");
        }

        System.out.println();
        System.out.println("Starting simulation...");
        System.out.println();

        // runs the full game: initial placements, then the main loop
        // each round: roll dice -> all players collect resources -> active player builds
        // game ends when someone hits 10 VP or max rounds reached
        sim.runSimulation();

        System.out.println();
        System.out.println("============================================================");
        System.out.println("  SIMULATION COMPLETE");
        System.out.println("============================================================");
    }
}
