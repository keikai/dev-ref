package io.keikai.devref.util;

import io.keikai.api.Ranges;
import io.keikai.api.model.*;
import io.keikai.model.SRow;

import java.util.*;
import java.util.stream.*;

public class EmptyRowHelper {

    static public List<Integer> findEmptyRows(Sheet sheet, int maxRow) {
        List<Integer> emptyRowIndexes = IntStream.rangeClosed(0, maxRow)
                .boxed().collect(Collectors.toList());

        Iterator<SRow> rowIterator = sheet.getInternalSheet().getRowIterator(0, maxRow);
        while (rowIterator.hasNext()) {
            SRow row = rowIterator.next();
            emptyRowIndexes.remove(Integer.valueOf(row.getIndex()));
        }
        return emptyRowIndexes;
    }

    static public void hideEmptyRows(Sheet sheet, List<Integer> emptyRowIndexes) {
        for (Integer rowIndex : emptyRowIndexes)
            Ranges.range(sheet, rowIndex, 0).toRowRange().setHidden(true);
    }
}