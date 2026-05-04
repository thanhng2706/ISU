package spreadsheet.Model.Cell;

import spreadsheet.Model.Expression.Expression;
import spreadsheet.Model.Observer.CellObserver;

/**
 * Composite pattern – common interface for Cell (leaf) and CellGroup (composite).
 * Allows expressions to treat a single cell and a range of cells uniformly.
 *
 * Also acts as the Subject in the Observer pattern.
 * Default implementations are no-ops so CellGroup doesn't need to override them.
 */
public interface CellComponent {
    double getCellValue();
    double setCellValue(double newValue);
    void setExpression(Expression expression);
    Expression getExpression();

    // Composite operations (unsupported on leaf Cell)
    void addCell(CellComponent cell);
    void removeCell(CellComponent cell);
    CellComponent getComponent(int index);

    // Observer pattern – subject side (Cell overrides; CellGroup uses no-ops)
    default void addObserver(CellObserver observer) {}
    default void removeObserver(CellObserver observer) {}
    default void notifyObservers() {}
}
