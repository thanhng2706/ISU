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

package spreadsheet.View.ViewModels;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import spreadsheet.Controller.Command.CommandManager;
import spreadsheet.Controller.Command.SetExpressionCommand;
import spreadsheet.Controller.Command.SetValueCommand;
import spreadsheet.Controller.ExpressionParser;
import spreadsheet.Model.CellRepository;
import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Expression.Expression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
                CellComponent cellComponent = CellRepository.getInstance().getReferenceCellComponent(r,c);
                CellViewModel cellViewModel = new CellViewModel(cellComponent);
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(r, c, 1, 1, "");
                cell.setEditable(true);
                cell.setItem("");

                // Value Property update cascade to cell property
                cellViewModel.valueProperty.addListener((obs, oldVal, newVal) -> {
                    if(cellViewModel.suppressViewUpdate) return;
                    cellViewModel.isCascadingUpdate = true;
                    cell.setItem(String.valueOf(cellViewModel.getValue()));
                    cellViewModel.isCascadingUpdate = false;
                });

                // User inputs update the model through the Command pattern
                cell.textProperty().addListener((obs, oldText, newText) -> {
                    if(cellViewModel.isCascadingUpdate) return;

                    if(newText.startsWith("=")){
                        try {
                            Expression expression = ExpressionParser.convertExpression(newText);
                            cellViewModel.expressionProperty.set(newText);
                            CommandManager.getInstance().executeCommand(
                                new SetExpressionCommand(cellViewModel.getModel(), expression)
                            );
                            cellViewModel.refreshValueProperty();
                        } catch (Exception e) {
                            // Incomplete or invalid formula while typing — ignore until complete
                        }
                    } else {
                        try {
                            double value = Double.parseDouble(newText);
                            CommandManager.getInstance().executeCommand(
                                new SetValueCommand(cellViewModel.getModel(), value)
                            );
                        } catch (NumberFormatException e) {
                            cellViewModel.suppressViewUpdate = true;
                            cellViewModel.getModel().setCellValue(0);
                            cellViewModel.expressionProperty.set(newText);
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
