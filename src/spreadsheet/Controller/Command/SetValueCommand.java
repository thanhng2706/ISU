package spreadsheet.Controller.Command;

import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Expression.Expression;

/**
 * Command pattern – concrete command for setting a cell to a plain numeric value.
 *
 * Captures the cell's previous state (value or expression) on construction so
 * that undo() can restore it exactly.
 */
public class SetValueCommand implements Command {
    private final CellComponent cell;
    private final double newValue;
    private final double oldValue;
    private final Expression oldExpression;

    public SetValueCommand(CellComponent cell, double newValue) {
        this.cell          = cell;
        this.newValue      = newValue;
        this.oldValue      = cell.getCellValue();
        this.oldExpression = cell.getExpression();
    }

    @Override
    public void execute() {
        cell.setCellValue(newValue);
    }

    @Override
    public void undo() {
        if (oldExpression != null) {
            cell.setExpression(oldExpression);
        } else {
            cell.setCellValue(oldValue);
        }
    }
}
