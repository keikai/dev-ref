package io.keikai.devref.analyzer;

import io.keikai.model.SCell;

public class CellProcessor extends Processor<SCell>{
    public CellProcessor(Processor processor) {
        super(processor);
        item = new ReportItem("Non-Empty Cell");
    }

    @Override
    public void process(SCell cell) {
        item.setCounter(item.getCounter()+1);
        toNext(cell);
    }

}
