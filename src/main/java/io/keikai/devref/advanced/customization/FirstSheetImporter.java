package io.keikai.devref.advanced.customization;

import io.keikai.model.SBook;
import io.keikai.range.*;

import java.io.*;
import java.net.URL;

/**
 * An example of custom importer which just keeps the first sheet of a book.
 * Watch out! This approach doesn't always work on every file e.g. a named range referenced to those removed sheets
 * will cause an exception.
 * <p>
 * Since Keikai 7.0, {@code io.keikai.importer.XlsxImporter} and its extractor hooks were removed
 * (importing is handled by the built-in engine). A custom importer now wraps the default
 * {@link SImporter} and post-processes the imported {@link SBook} instead.
 */
public class FirstSheetImporter implements SImporter {
    private final SImporter delegate = SImporters.getImporter();

    @Override
    public SBook imports(InputStream is, String bookName) throws IOException {
        return keepFirstSheetOnly(delegate.imports(is, bookName));
    }

    @Override
    public SBook imports(File file, String bookName) throws IOException {
        return keepFirstSheetOnly(delegate.imports(file, bookName));
    }

    @Override
    public SBook imports(URL url, String bookName) throws IOException {
        return keepFirstSheetOnly(delegate.imports(url, bookName));
    }

    private SBook keepFirstSheetOnly(SBook book) {
        while (book.getNumOfSheet() > 1) {
            book.deleteSheet(book.getSheet(book.getNumOfSheet() - 1));
        }
        return book;
    }
}
