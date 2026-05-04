package spreadsheet.Controller.Command;

/**
 * Command pattern – base interface.
 * Every user action that modifies a cell is wrapped in a Command so it can
 * be undone and redone by the CommandManager.
 */
public interface Command {
    /** Apply the action. */
    void execute();

    /** Reverse the action, restoring the cell to its prior state. */
    void undo();
}
