package spreadsheet.Model.Expression;

import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Cell.CellGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Leaf node – represents a cell range reference (e.g. A1:B3).
 * Overrides getAllValues() to expand the range into individual values,
 * which lets aggregate operators (SUM, COUNT, AVE) work correctly.
 */
public class CellGroupExpression implements Expression {
    private final CellGroup group;

    public CellGroupExpression(CellGroup group) {
        this.group = group;
    }

    /** Returns the sum of all cells (default scalar behavior). */
    @Override
    public double evaluate() {
        return group.getCellValue();
    }

    /** Expands the range — each cell contributes one value. */
    @Override
    public List<Double> getAllValues() {
        return group.getAllValues();
    }

    /** Returns all cells in the range for observer registration. */
    @Override
    public List<CellComponent> getReferencedCells() {
        return group.getAllCells();
    }

    @Override
    public void addOperand(Expression... expression) {
        throw new UnsupportedOperationException("Cell group reference has no operands");
    }

    @Override
    public ArrayList<Expression> getOperands() {
        return new ArrayList<>();
    }
}
