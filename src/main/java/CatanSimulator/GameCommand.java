package CatanSimulator;

/**
 * Command Pattern - Command Interface (R3.1: Undo/Redo)
 *
 * Every reversible game action implements this interface.
 * execute() performs the action; undo() reverses it completely.
 */
public interface GameCommand {
    /**
     * Perform the action. Returns true if successful.
     */
    boolean execute();

    /**
     * Reverse the action, restoring all game state to before execute() was called.
     */
    void undo();
}
