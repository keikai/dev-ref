package io.keikai.devref.performance;

import io.keikai.client.api.*;
import io.keikai.client.api.event.Events;
import io.keikai.devref.KeikaiCase;
import io.keikai.util.Converter;

public class FillMillion implements KeikaiCase {
    private Spreadsheet spreadsheet;

    @Override
    public void init(String keikaiEngineAddress) {
        spreadsheet = Keikai.newClient(keikaiEngineAddress);
    }

    @Override
    public String getJavaScriptURI(String domId) {
        return spreadsheet.getURI(domId);
    }

    /**
     * set cell values row by row (1000 columns * 1000 rows)
     */
    @Override
    public void run() {
        //Setting a row value at a time.
        int rowCount = 1000;
        int columnCount = 1000;
        Range cell = spreadsheet.getRange(rowCount, columnCount);
        cell.activate();
        cell.setValue("click to start");
        spreadsheet.addEventListener(Events.ON_CELL_CLICK, rangeEvent -> {
            if (rangeEvent.getRow() == rowCount && rangeEvent.getColumn() == columnCount) {
                for (int row = 0; row < rowCount; row++) {
                    String[] values = new String[rowCount];
                    for (int column = 0; column < columnCount; column++) {
                        values[column] = (Converter.numToAbc(column) + (row + 1));
                    }
                    Range range = spreadsheet.getRange(row, 0, 1, 1000);
                    range.setNumberFormat("@"); //save smart input parsing time
                    range.setValues(values);
                }
            }
        });
    }
}
