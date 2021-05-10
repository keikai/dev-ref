package io.keikai.devref.analyzer;

import io.keikai.api.Ranges;
import io.keikai.api.model.Sheet;

public class NameProcessor extends Processor<Sheet>{
    public NameProcessor(Processor processor) {
        super(processor);
        item = new ReportItem("Named Range");
    }

    @Override
    public void process(Sheet sheet) {
        item.setCounter(item.getCounter() + Ranges.getNames(sheet).size());
        toNext(sheet);
    }

}
