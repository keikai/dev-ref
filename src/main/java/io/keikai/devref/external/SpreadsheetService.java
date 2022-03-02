package io.keikai.devref.external;

import io.keikai.api.*;
import io.keikai.api.model.*;
import org.zkoss.zk.ui.WebApps;

import java.io.*;
import java.util.Optional;

/**
 * Provides methods to access a {@link Book}.
 * If this class provides methods to set cell values, then it requires a write lock.
 */
public class SpreadsheetService {
    private static final String DEFAULT_BOOK_FOLDER = "/WEB-INF/books/";
    private Book book;
    static private final Importer importer = Importers.getImporter();

    public SpreadsheetService(String fileName) throws IOException {
        importBook(fileName);
    }

    private void importBook(String fileName) throws IOException {
        book = importer.imports(new File(WebApps.getCurrent().getRealPath(DEFAULT_BOOK_FOLDER + fileName)), fileName);
    }


    public String getCellValue(int rowIndex, int columnIndex) {
        Optional cellValue = Optional.ofNullable(Ranges.range(book.getSheetAt(0), rowIndex, columnIndex).getCellValue());
        cellValue.orElse("");
        return cellValue.get().toString();
    }
}
