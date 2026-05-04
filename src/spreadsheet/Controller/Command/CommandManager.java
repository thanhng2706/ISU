package spreadsheet.Controller.Command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command pattern – invoker / history manager.
 *
 * Maintains two stacks:
 *   undoStack – commands that have been executed and can be undone
 *   redoStack – commands that have been undone and can be re-applied
 *
 * Singleton so the same history is shared across the entire application.
 *
 * Usage:
 *   CommandManager.getInstance().executeCommand(new SetValueCommand(cell, 42));
 *   CommandManager.getInstance().undo();
 *   CommandManager.getInstance().redo();
 */
public class CommandManager {
    private static CommandManager instance;

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    private CommandManager() {}

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    /** Reset the singleton (used in tests). */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Execute a command and push it onto the undo stack.
     * Any existing redo history is cleared because a new action invalidates it.
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undo the most recent command and push it onto the redo stack.
     * Does nothing if there is nothing to undo.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Re-apply the most recently undone command and push it back onto the undo stack.
     * Does nothing if there is nothing to redo.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}
