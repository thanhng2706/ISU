/*
 * Copyright (c) 2025 Sakire Arslan Ay and Quan Dinh
 *
 * This file was developed for educational purposes as part of CS4233:
 * Object-Oriented Analysis & Design at Worcester Polytechnic Institute.
 *
 * The project is based on the "Spreadsheet" application developed by Quan Dinh.
 *
 * All rights reserved. Redistribution and modification outside the scope
 * of this course are not permitted without prior written permission.
 */

package spreadsheet.Model.Cell;

import spreadsheet.Model.Expression.Expression;
import spreadsheet.Model.Observer.CellObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite pattern – leaf node.
 * Observer pattern – acts as both Subject (notifies observers when value changes)
 *                    and Observer (re-evaluates when a referenced cell changes).
 */
public class Cell implements CellComponent, CellObserver {
    private double value = 0;
    private Expression expression;

    // Observer pattern – subject side
    private final List<CellObserver> observers = new ArrayList<>();

    // Tracks which cells this cell depends on (for unregistering on expression change)
    private final List<CellComponent> dependencies = new ArrayList<>();

    public Cell(double value) {
        this.value = value;
    }

    // =========================================================
    //  CellComponent – value / expression
    // =========================================================

    @Override
    public double getCellValue() {
        return this.value;
    }

    @Override
    public double setCellValue(double newValue) {
        unregisterDependencies();
        this.expression = null;
        this.value = newValue;
        notifyObservers();
        return this.value;
    }

    @Override
    public void setExpression(Expression expression) {
        unregisterDependencies();
        this.expression = expression;
        if (expression != null) {
            // Register as observer on every cell this expression references
            for (CellComponent dep : expression.getReferencedCells()) {
                dep.addObserver(this);
                dependencies.add(dep);
            }
            this.value = expression.evaluate();
        }
        notifyObservers();
    }

    @Override
    public Expression getExpression() {
        return this.expression;
    }

    // =========================================================
    //  CellComponent – composite (unsupported for leaf)
    // =========================================================

    @Override
    public void addCell(CellComponent cell) {
        throw new UnsupportedOperationException("Cannot add children to a single Cell");
    }

    @Override
    public void removeCell(CellComponent cell) {
        throw new UnsupportedOperationException("Cannot remove children from a single Cell");
    }

    @Override
    public CellComponent getComponent(int index) {
        throw new UnsupportedOperationException("Single Cell has no children");
    }

    // =========================================================
    //  Observer pattern – Subject side
    // =========================================================

    @Override
    public void addObserver(CellObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(CellObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (CellObserver observer : new ArrayList<>(observers)) {
            observer.onCellUpdated(this);
        }
    }

    // =========================================================
    //  Observer pattern – Observer side
    //  Called when a cell this cell depends on changes value
    // =========================================================

    @Override
    public void onCellUpdated(CellComponent source) {
        if (this.expression != null) {
            this.value = this.expression.evaluate();
            notifyObservers(); // cascade to dependents
        }
    }

    // =========================================================
    //  Private helpers
    // =========================================================

    private void unregisterDependencies() {
        for (CellComponent dep : dependencies) {
            dep.removeObserver(this);
        }
        dependencies.clear();
    }
}
