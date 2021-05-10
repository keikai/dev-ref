package io.keikai.devref.analyzer;

import io.keikai.api.model.Sheet;

public class SheetProcessor extends Processor<Sheet>{
    public SheetProcessor(Processor processor) {
        super(processor);
        item = new ReportItem("Sheets");
    }

    @Override
    public void process(Sheet sheet) {
        item.setCounter(item.getCounter()+1);
        toNext(sheet);
    }

}
