package io.keikai.devref.util;

import io.keikai.api.Range;
import io.keikai.ui.event.CellMouseEvent;

public class RangeHelper {

    public static boolean isRangeClicked(CellMouseEvent event, Range range){
        return event.getRow() >= range.getRow() && event.getRow() <= range.getLastRow()
                && event.getColumn() >= range.getColumn() && event.getColumn() <= range.getLastColumn();
    }

    /**
     * fill multiple values cell by cell in one row starting from the specified cell, then fill the next cell on right
     * e.g. A1, B1, C1...
     * @param startingCell the 1st cell to fill a value
     * @param values cell values to fill
     */
    public static void setValuesInRow(Range startingCell, Object... values){
        for (Object value : values) {
            startingCell.setCellValue(value);
            startingCell = startingCell.toShiftedRange(0, 1);
        }
    }
}
