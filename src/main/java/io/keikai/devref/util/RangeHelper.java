package io.keikai.devref.util;

import io.keikai.api.*;
import io.keikai.ui.event.*;


public class RangeHelper {

    public static boolean isRangeClicked(CellMouseEvent event, Range range){
        return event.getRow() >= range.getRow() && event.getRow() <= range.getLastRow()
                && event.getColumn() >= range.getColumn() && event.getColumn() <= range.getLastColumn();
    }

    public static Range getTargetRange(CellEvent event){
        return Ranges.range(event.getSheet(), event.getRow(), event.getColumn());
    }

    public static Range getTargetRange(CellMouseEvent event){
        return Ranges.range(event.getSheet(), event.getRow(), event.getColumn());
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
            if (startingCell.hasMergedCell()){
                startingCell = startingCell.toShiftedRange(0, startingCell.getMergedRegion().getColumnCount());
            }else {
                startingCell = startingCell.toShiftedRange(0, 1);
            }

        }
    }

    public static String[] getValuesInRow(Range range){
        String[] values = new String[range.getColumnCount()];
        Range startingCell = range.toCellRange(0, 0);
        for (int n =0 ; n < values.length ; n++) {
            values[n] = startingCell.getCellValue().toString();
            startingCell = startingCell.toShiftedRange(0, 1);
        }
        return  values;
    }
}
