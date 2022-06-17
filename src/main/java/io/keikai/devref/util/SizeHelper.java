package io.keikai.devref.util;

import io.keikai.api.Ranges;
import io.keikai.api.model.*;
import io.keikai.model.CellRegion;
import org.slf4j.*;

/**
 * reset row height and column width to the default size.
 * Benefits:
 * - reduce CSS rules produced by custom height and width which improve browser rendering performance
 */
public class SizeHelper {
    static Logger logger = LoggerFactory.getLogger(SizeHelper.class);
    private static CellRegion dataRegion;

    static public void resetDefaultHeightWidth(Book book) {
        for (int n = 0; n < book.getNumberOfSheets(); n++) {
            Sheet sheet = book.getSheetAt(n);
            dataRegion = Ranges.range(sheet).getDataRegion();
            resetHeight(sheet);
            resetWidth(sheet);
        }
        logger.info("reset height and width in {} sheets", book.getNumberOfSheets());
    }

    private static void resetWidth(Sheet sheet) {
        Ranges.range(sheet, 0, dataRegion.getColumn(), 0, dataRegion.getLastColumn()).setColumnWidth(sheet.getInternalSheet().getDefaultColumnWidth());
    }

    private static void resetHeight(Sheet sheet) {
        Ranges.range(sheet, dataRegion.getRow(), 0, dataRegion.getLastRow(), 0).setRowHeight(sheet.getInternalSheet().getDefaultRowHeight(), false);
    }
}
