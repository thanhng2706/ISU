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

package spreadsheet.View.ViewModels;

import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Observer.CellObserver;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CellViewModel implements CellObserver {

    private CellComponent model;
    public DoubleProperty valueProperty = new SimpleDoubleProperty();
    public StringProperty expressionProperty = new SimpleStringProperty();

    // Flags for updating
    public boolean isCascadingUpdate = false;
    public boolean suppressViewUpdate = false;

    /**
     * -------------------------------
     *          CONSTRUCTOR
     * -------------------------------
     */
    public CellViewModel(CellComponent model) {
        this.model = model;
        valueProperty.set(this.model.getCellValue());
        expressionProperty.set("");
        this.model.addObserver(this); // register so UI refreshes when cell value changes
    }

    public void refreshValueProperty(){
        this.valueProperty.set(this.model.getCellValue());
    }

    @Override
    public void onCellUpdated(CellComponent source) {
        refreshValueProperty();
    }
    /**
     * ----------------------------------------
     *          GETTERS AND SETTERS
     * ----------------------------------------
     */

    public double getValue(){
        return valueProperty.get();
    }

    public String getExpression(){
        return expressionProperty.get();
    }

    public StringProperty getExpressionProperty(){
        return expressionProperty;
    }

    public DoubleProperty getValueProperty(){
        return valueProperty;
    }

    public CellComponent getModel(){
        return model;
    }
}
