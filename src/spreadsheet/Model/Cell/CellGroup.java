package spreadsheet.Model.Cell;

import spreadsheet.Model.Expression.Expression;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite pattern – composite node.
 * Represents a rectangular range of cells (e.g. A1:C3).
 * Can hold individual Cells or nested CellGroups.
 */
public class CellGroup implements CellComponent {
    private final ArrayList<CellComponent> children = new ArrayList<>();

    @Override
    public void addCell(CellComponent cell) {
        children.add(cell);
    }

    @Override
    public void removeCell(CellComponent cell) {
        children.remove(cell);
    }

    @Override
    public CellComponent getComponent(int index) {
        return children.get(index);
    }

    /**
     * Returns all leaf CellComponents by flattening nested groups.
     * Used by CellGroupExpression.getReferencedCells() for observer registration.
     */
    public List<CellComponent> getAllCells() {
        List<CellComponent> cells = new ArrayList<>();
        for (CellComponent child : children) {
            if (child instanceof CellGroup cg) {
                cells.addAll(cg.getAllCells());
            } else {
                cells.add(child);
            }
        }
        return cells;
    }

    /**
     * Returns all leaf Cell values by flattening nested groups.
     */
    public List<Double> getAllValues() {
        List<Double> values = new ArrayList<>();
        for (CellComponent child : children) {
            if (child instanceof CellGroup cg) {
                values.addAll(cg.getAllValues());
            } else {
                values.add(child.getCellValue());
            }
        }
        return values;
    }

    /**
     * Not meaningful for a group — returns sum of all cells.
     */
    @Override
    public double getCellValue() {
        return children.stream()
                .mapToDouble(CellComponent::getCellValue)
                .sum();
    }

    @Override
    public double setCellValue(double newValue) {
        throw new UnsupportedOperationException("Cannot set value on a CellGroup");
    }

    @Override
    public void setExpression(Expression expression) {
        throw new UnsupportedOperationException("Cannot set expression on a CellGroup");
    }

    @Override
    public Expression getExpression() {
        return null;
    }
}
