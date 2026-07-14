package io.keikai.devref.analyzer.counter;

import io.keikai.api.model.Book;
import io.keikai.model.SBook;

public class StyleCounter extends Counter<Book> {
    public StyleCounter(Counter processor) {
        super(processor);
        name = "Cell Style";
    }
    @Override
    public void process(Book object) {
        // Since Keikai 7.0, BookImpl and its cell style tables are no longer exposed;
        // count the styles available from the SBook interface instead.
        SBook internalBook = object.getInternalBook();
        counter = internalBook.getDefaultCellStyles().size() + internalBook.getNamedStyles().size();
    }
}
