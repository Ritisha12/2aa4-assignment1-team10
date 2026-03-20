package CatanSimulator;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command Pattern - Invoker (R3.1: Undo/Redo)
 *
 * Manages two stacks:
 *   undoStack - commands that have been executed (available to undo)
 *   redoStack - commands that have been undone (available to redo)
 *
 * Executing a new command always clears the redo stack.
 * History is capped at maxHistorySize to prevent unbounded memory use.
 */
public class CommandHistory {

    private static final int DEFAULT_MAX_HISTORY = 50;

    private final Deque<GameCommand> undoStack;
    private final Deque<GameCommand> redoStack;
    private final int maxHistorySize;

    public CommandHistory() {
        this(DEFAULT_MAX_HISTORY);
    }

    public CommandHistory(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Execute a command and push it onto the undo stack.
     * Clears the redo stack — a new action invalidates the redo branch.
     * @return true if the command executed successfully
     */
    public boolean executeCommand(GameCommand command) {
        boolean success = command.execute();
        if (success) {
            undoStack.push(command);
            redoStack.clear();
            // enforce max history by dropping the oldest entry if needed
            if (undoStack.size() > maxHistorySize) {
                dropOldest(undoStack);
            }
        }
        return success;
    }

    /**
     * Undo the most recently executed command.
     * @return true if there was something to undo
     */
    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        GameCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    /**
     * Redo the most recently undone command.
     * @return true if there was something to redo
     */
    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        GameCommand command = redoStack.pop();
        boolean success = command.execute();
        if (success) {
            undoStack.push(command);
        }
        return success;
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    /** Clear both stacks (e.g. on game reset). */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public int undoSize() { return undoStack.size(); }
    public int redoSize() { return redoStack.size(); }

    // Remove the oldest (bottom) element from the deque
    private void dropOldest(Deque<GameCommand> deque) {
        GameCommand[] arr = deque.toArray(new GameCommand[0]);
        deque.clear();
        for (int i = 0; i < arr.length - 1; i++) {
            deque.add(arr[i]);
        }
    }
}
