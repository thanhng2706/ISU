package spreadsheet.Model.Expression;

import spreadsheet.Model.Cell.CellComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * Leaf node – represents a reference to a single cell (e.g. B2).
 * evaluate() returns the cell's current value so that expressions
 * always reflect the latest cell state.
 */
public class CellReferenceExpression implements Expression {
    private final CellComponent cell;

    public CellReferenceExpression(CellComponent cell) {
        this.cell = cell;
    }

    @Override
    public double evaluate() {
        return cell.getCellValue();
    }

    @Override
    public List<CellComponent> getReferencedCells() {
        return List.of(cell);
    }

    @Override
    public void addOperand(Expression... expression) {
        throw new UnsupportedOperationException("Cell reference has no operands");
    }

    @Override
    public ArrayList<Expression> getOperands() {
        return new ArrayList<>();
    }
}
