package spreadsheet.Model.Observer;

import spreadsheet.Model.Cell.CellComponent;

/**
 * Observer pattern – observer interface.
 * Implemented by anything that needs to react when a cell's value changes
 * (e.g. another Cell whose expression references it, or CellViewModel for UI refresh).
 */
public interface CellObserver {
    void onCellUpdated(CellComponent source);
}
