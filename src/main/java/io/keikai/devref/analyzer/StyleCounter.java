package io.keikai.devref.analyzer;

import io.keikai.api.model.Book;
import io.keikai.model.impl.BookImpl;

public class StyleCounter extends Counter<Book> {
    public StyleCounter(Counter processor) {
        super(processor);
        item = new ReportItem("Cell Style");
    }
    @Override
    public void process(Book book) {
        BookImpl internalBook = (BookImpl) book.getInternalBook();
        item.setCounter(internalBook.getCellStyleTable().size() + internalBook.geImmutableCellStyleTable().size());
    }
}
