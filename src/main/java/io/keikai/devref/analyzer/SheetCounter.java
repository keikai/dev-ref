package io.keikai.devref.analyzer;

import io.keikai.api.model.Sheet;

public class SheetCounter extends Counter<Sheet> {
    public SheetCounter(Counter processor) {
        super(processor);
        item = new ReportItem("Sheets");
    }

    @Override
    public void process(Sheet sheet) {
        item.setCounter(item.getCounter()+1);
        toNext(sheet);
    }

}
