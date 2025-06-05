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

package spreadsheet.Model.Cell;

import spreadsheet.Model.Expression.Expression;

public class Cell {
    private double value = 0;
    private Expression expression;

    public Cell(double value){
        this.value = value;
    }
    public double getCellValue(){
        return this.value;
    }
    public double setCellValue(double newValue){
        this.expression = null;
        this.value = newValue;
        return this.value;
    }
    public void setExpression(Expression expression){
        this.expression = expression;
        this.value = expression.evaluate();
    }

    public Expression getExpression(){
        return this.expression;
    }
}