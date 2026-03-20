package CatanSimulator;

// Demonstrator for the Settlers of Catan simulator.
// Running this will set up the board, create 1 human player + 3 AI players,
// and simulate a full Assignment 3 game.
//
// Key things to observe:
// - Board is set up from MapSetup with 19 tiles, 54 nodes, and edges (R1.1)
// - Player 0 is human-controlled through command-line commands (R2.1)
// - The game exports target/visualizer/state.json for the instructor visualizer (R2.2, R2.3)
// - Type "go" at the advance prompt to step to the next player's turn (R2.4)
// - Human commands are: roll, list, build settlement <node>, build city <node>,
//   build road <fromNode>,<toNode>, undo, redo, and go
// - Rolling a 7 triggers the simplified robber flow (R2.5)
// - Game runs for configurable number of rounds or until 10 VP (R1.4, R1.5)
//
// A3 Task 1 - Command Pattern (R3.1: Undo/Redo):
// - Every human build action (settlement, city, road) is wrapped in a GameCommand object
// - Commands are executed through CommandHistory, which maintains an undo stack and redo stack
// - Type "undo" during your turn to reverse your last build action (resources are restored)
// - Type "redo" to re-apply an undone action
// - The CommandHistory is cleared at the start of each human turn
// - AI turns use direct build methods (undo/redo is a human player feature)
//
// To watch the board, run the instructor visualizer from target/visualizer so it
// watches the generated state.json file there, for example:
//   cd target/visualizer
//   python /path/to/2aa4-2026-base/assignments/visualize/light_visualizer.py \
//     /path/to/2aa4-2026-base/assignments/visualize/base_map.json --watch
public class Demonstrator {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  SETTLERS OF CATAN SIMULATOR");
        System.out.println("  SFWRENG 2AA4 - Assignment 3");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("  A3 Task 1: Command Pattern for Undo/Redo (R3.1)");
        System.out.println("  During your turn, type:");
        System.out.println("    undo  -> reverses your last build action");
        System.out.println("    redo  -> re-applies the last undone action");
        System.out.println("============================================================");
        System.out.println();

        // creates the board, 4 players (1 human + 3 AI), dice, logger,
        // and the CommandHistory that powers undo/redo
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
        // human player builds go through CommandHistory.executeCommand() so they can be undone
        // game ends when someone hits 10 VP or max rounds reached
        sim.runSimulation();

        System.out.println();
        System.out.println("============================================================");
        System.out.println("  SIMULATION COMPLETE");
        System.out.println("============================================================");
    }
}