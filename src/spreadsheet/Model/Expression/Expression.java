/*
 * Copyright (c) 2025 Sakire Arslan Ay and Quan Dinh
 *
 * This file was developed for educational purposes as part of CS4233:
 * Object-Oriented Analysis & Design at Worcester Polytechnic Institute.
 *
 * The project is based on the “Spreadsheet” application developed by Quan Dinh.
 *
 * All rights reserved. Redistribution and modification outside the scope
 * of this course are not permitted without prior written permission.
 */

package spreadsheet.Model.Expression;

import java.util.ArrayList;
import java.util.List;
import spreadsheet.Model.Cell.CellComponent;

public interface Expression {
    double evaluate();

    void addOperand(Expression... expression);
    ArrayList<Expression> getOperands();

    /**
     * Returns all scalar values this expression expands to.
     * - Constants / arithmetic: single-element list of evaluate().
     * - Cell group reference: one value per cell in the range.
     * Used by aggregate operators to handle ranges correctly.
     */
    default List<Double> getAllValues() {
        return List.of(evaluate());
    }

    /**
     * Observer pattern support — returns all CellComponents directly
     * referenced by this expression (recursively).
     * Used by Cell.setExpression() to register as observer on dependencies.
     */
    default List<CellComponent> getReferencedCells() {
        List<CellComponent> refs = new ArrayList<>();
        for (Expression op : getOperands()) {
            refs.addAll(op.getReferencedCells());
        }
        return refs;
    }
}
