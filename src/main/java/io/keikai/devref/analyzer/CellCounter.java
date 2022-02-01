package io.keikai.devref.analyzer;

import io.keikai.model.SCell;

public class CellCounter extends Counter<SCell> {
    public CellCounter(Counter processor) {
        super(processor);
        item = new ReportItem("Non-Empty Cell");
    }

    @Override
    public void process(SCell cell) {
        item.setCounter(item.getCounter()+1);
        toNext(cell);
    }

}
