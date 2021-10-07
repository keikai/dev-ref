package io.keikai.devref.external;

import io.keikai.api.*;
import io.keikai.api.model.*;
import org.zkoss.zk.ui.WebApps;

import java.io.*;
import java.util.Optional;

/**
 * the part of the example to demonstrate the example to handle external API request to access a {@link Book}.
 */
public class SpreadsheetService {
    private Book book;
    static private final Importer importer = Importers.getImporter();

    public SpreadsheetService() throws IOException {
        importBook();
    }

    private void importBook() throws IOException {
        book = importer.imports(new File(WebApps.getCurrent().getRealPath("/WEB-INF/books/demo_sample.xlsx")), "demo_sample.xlsx");
    }


    public String getCellValue(int rowIndex, int columnIndex) {
        Optional cellValue = Optional.ofNullable(Ranges.range(book.getSheetAt(0), rowIndex, columnIndex).getCellValue());
        cellValue.orElse("");
        return cellValue.get().toString();
    }
}
