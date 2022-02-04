package io.keikai.devref.analyzer.counter;

import io.keikai.api.model.Book;
import io.keikai.model.impl.BookImpl;

public class StyleCounter extends Counter<Book> {
    public StyleCounter(Counter processor) {
        super(processor);
        name = "Cell Style";
    }
    @Override
    public void process(Book object) {
        BookImpl internalBook = (BookImpl) object.getInternalBook();
        counter = internalBook.getCellStyleTable().size() + internalBook.geImmutableCellStyleTable().size();
    }
}
