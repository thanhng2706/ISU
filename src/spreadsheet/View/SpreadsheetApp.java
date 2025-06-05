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

package spreadsheet.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import spreadsheet.View.ViewModels.GridViewModel;


public class SpreadsheetApp extends Application{
    private SpreadsheetView spreadsheetView;

    public SpreadsheetApp(){
        GridViewModel grid = new GridViewModel();
        spreadsheetView = new SpreadsheetView(grid.getGrid());

        for (SpreadsheetColumn column : spreadsheetView.getColumns()) {
            column.setPrefWidth(200);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();
        VBox.setVgrow(spreadsheetView, Priority.ALWAYS);
        vbox.getChildren().addAll(spreadsheetView);
        
        Scene scene = new Scene(vbox, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Spreadsheet");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
