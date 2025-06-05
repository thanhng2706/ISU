package spreadsheet.Model;
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

import java.util.HashMap;
import spreadsheet.Model.Cell.Cell;

/**
 * A singleton central data point to fetch CellComponents.
 * Each CellComponent is identified by its CellCoord.
 * 
 * Consist of 100 ROWS and 20 COLUMNS
 * 
 * Singleton pattern allows the repository to be consistent across all parts of the code.
 */
public class CellRepository {
    private static CellRepository instance;
    private static HashMap<CellCoord, Cell> cellMap; //Every CC has a unique CellCoord

    public static final int ROWS = 100;
    public static final int COLUMNS = 20;

    /**
     * private constructor to prevent accidentally reset the data point
     */
    private CellRepository(){
        cellMap = new HashMap<>();
        initialize();
    }

    /**
     * get the reference/instance of the CellRepository
     * How to use:
     * Option 1:
     *      CellRepository repo = CellRepository.getInstance();
     *      repo.getReferenceCellComponent(row, col);
     * 
     * Option 2:
     *      CellRepository.getInstance().getReferencedCellComponent(row, col);
     * 
     * @return the CellRepository instance
     */
    public static synchronized CellRepository getInstance() {
        if(instance == null){
            instance = new CellRepository();
        }
        return instance;
    }

    /**
     * Reset the instance (clear all data) -- likely will not use outside of testing
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Initialize the Hashmap with empty CellComponent
     */
    private static void initialize(){
        for(int row=0; row<ROWS; row++){
            for(int col=0; col<COLUMNS; col++){
                CellCoord coord = new CellCoord(row, col);
                Cell cell = new Cell(0);
                cellMap.put(coord, cell);
            }
        }
    }

    /**
     * get the referenced CellComponent within the hashmap based on its CellCoord
     * @param row the numerical row
     * @param col the numerical column (0=a, 1=b, etc.)
     * @return the referenced CellComponent
     */
    public Cell getReferenceCellComponent(int row, int col){
        return cellMap.get(new CellCoord(row, col));
    }
}
