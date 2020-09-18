package io.keikai.devref.advanced.customization;

import io.keikai.importer.*;
import io.keikai.model.SSheet;

import java.util.List;

/**
 * An example of custom importer which just imports the first sheet of a book.
 * Watch out! This approach doesn't always work on every file e.g. a named range referenced to those not-imported sheet
 * will cause an exception.
 */
public class FirstSheetImporter extends XlsxImporter {
    @Override
    protected void importSheets(List<XlsxExtractor.XlsxSheetExtractor> sheets) {
        XlsxExtractor.XlsxSheetExtractor xSheet = sheets.get(0);
        SSheet sheet = this.importSheet(xSheet);
        this.importTables(xSheet, sheet);
        for (int i = 1 ; i < sheets.size() ; i++){
            sheets.remove(i);
        }
    }
}
