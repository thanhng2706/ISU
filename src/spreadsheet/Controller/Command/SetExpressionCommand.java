package spreadsheet.Controller.Command;

import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Expression.Expression;

/**
 * Command pattern – concrete command for setting a cell to a formula expression.
 *
 * Captures the cell's previous state (value or expression) on construction so
 * that undo() can restore it exactly.
 */
public class SetExpressionCommand implements Command {
    private final CellComponent cell;
    private final Expression newExpression;
    private final double oldValue;
    private final Expression oldExpression;

    public SetExpressionCommand(CellComponent cell, Expression newExpression) {
        this.cell          = cell;
        this.newExpression = newExpression;
        this.oldValue      = cell.getCellValue();
        this.oldExpression = cell.getExpression();
    }

    @Override
    public void execute() {
        cell.setExpression(newExpression);
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
