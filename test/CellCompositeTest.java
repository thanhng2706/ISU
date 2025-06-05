import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import spreadsheet.Model.Cell.Cell;
import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Cell.CellGroup;

public class CellCompositeTest {

    @Test
    public void testCell(){
        CellComponent cellA = new Cell(10);
        double value = cellA.getCellValue();
        assertEquals(value, 10);

        cellA.setCellValue(20);
        value = cellA.getCellValue();
        assertEquals(value, 20);
    }

    @Test
    public void testCellGroup_SingleCells(){
        CellComponent cellA = new Cell(10);
        CellComponent cellB = new Cell(20);

        CellComponent group = new CellGroup();

        group.addCell(cellA);
        group.addCell(cellB);

        CellComponent cellATest = group.getComponent(0);
        assertEquals(cellATest, cellA);

    }

    @Test
    public void testCellGroup_NestedCellGroups(){
        CellComponent cellA = new Cell(10);
        CellComponent cellB = new Cell(20);
        CellComponent cellC = new Cell(30);

        CellComponent groupA = new CellGroup();
        CellComponent groupB = new CellGroup();

        groupA.addCell(cellA);
        groupA.addCell(cellB);
        groupB.addCell(cellC);
        groupB.addCell(groupA);

        CellComponent cellCTest = groupB.getComponent(0);
        CellComponent groupATest = groupB.getComponent(1);
        CellComponent cellATest = groupATest.getComponent(0);

        assertEquals(cellCTest, cellC);
        assertEquals(groupATest, groupA);
        assertEquals(cellATest, cellA);

    }

    @Test
    public void testCellGroup_RemoveCells(){
        CellComponent cellA = new Cell(10);
        CellComponent cellB = new Cell(20);

        CellComponent group = new CellGroup();

        group.addCell(cellA);
        group.addCell(cellB);

        CellComponent cellATest = group.getComponent(0);
        assertEquals(cellATest, cellA);

        group.removeCell(cellA);
        CellComponent cellBTest = group.getComponent(0);
        assertEquals(cellBTest, cellB);

    }

}
