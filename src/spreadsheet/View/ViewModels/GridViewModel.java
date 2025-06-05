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

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import spreadsheet.Model.Parser.ExpressionParser;
import spreadsheet.Model.CellRepository;
import spreadsheet.Model.Cell.Cell;
import spreadsheet.Model.Expression.Expression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// removed "extends Region"
public class GridViewModel {
    private static int numCols;
    private static int numRows;
    private static GridBase grid;

    /**
     * -------------------------------
     *          CONSTRUCTOR
     * -------------------------------
     */
    public GridViewModel() {
        numRows = CellRepository.ROWS;
        numCols = CellRepository.COLUMNS;
        grid = new GridBase(numRows, numCols);
        initialize();
    }

    /**
     * -------------------------------
     *          INITIALIZATIONS
     * -------------------------------
     */
    public static void initialize() {
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        for(int r=0; r<numRows; r++){
            ObservableList<SpreadsheetCell> rowList = FXCollections.observableArrayList();

            for(int c=0; c<numCols; c++){
                Cell cellComponent = CellRepository.getInstance().getReferenceCellComponent(r,c);
                CellViewModel cellViewModel = new CellViewModel(cellComponent);
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(r, c, 1, 1, "");
                cell.setEditable(true);
                cell.setItem("");

                // Value Property update cascade to cell property
                cellViewModel.valueProperty.addListener((obs, oldText, newText) -> {
                    //Suppress the viewModel to not update even though valueProperty did
                    // EX: Reseting the cell and do not want to display the number 0.0
                    if(cellViewModel.suppressViewUpdate) return;

                    //Set the flag for the textProperty to not trigger twice
                    cellViewModel.isCascadingUpdate = true;
                    cell.setItem(String.valueOf(cellViewModel.getValue())); //Set the textProperty of cell to new value
                    cellViewModel.isCascadingUpdate = false;
                });

                // User inputs (or general update to the cell) and update the view model
                cell.textProperty().addListener((obs, oldText, newText) -> {
                    //Suppress the viewModel to not re-trigger from valueProperty updating
                    // EX: Value property updates from other cell changes, not user inputs
                    if(cellViewModel.isCascadingUpdate) return;

                    // Parse inputs to an expression
                    if(newText.startsWith("=")){
                        Expression expression = ExpressionParser.convertExpression(newText);
                        cellViewModel.expressionProperty.set(newText);
                        cellViewModel.getModel().setExpression(expression);
                        cellViewModel.refreshValueProperty();
                    }else{
                        // Parse inputs to a double
                        try {
                            double value = Double.parseDouble(newText);
                            cellViewModel.getModel().setCellValue(value);
                        } catch (NumberFormatException e) {
                            // Just display inputs otherwise if it is not a number nor an expression
                            // Set the flag for valueProperty resetting, and not set the textProperty to 0.0
                            cellViewModel.suppressViewUpdate = true;
                            cellViewModel.getModel().setCellValue(0); //reset model
                            cellViewModel.expressionProperty.set(newText); //base text
                            cellViewModel.suppressViewUpdate = false;
                        }
                    }
                });

                rowList.add(cell);
            }
            rows.add(rowList);
        }
        grid.setRows(rows);
    }

    public GridBase getGrid(){
        return grid;
    }

}