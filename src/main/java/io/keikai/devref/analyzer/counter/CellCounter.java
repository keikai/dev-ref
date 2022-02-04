package io.keikai.devref.analyzer.counter;

import io.keikai.model.SCell;

public class CellCounter extends Counter<SCell> {
    public CellCounter(Counter processor) {
        super(processor);
        name = "Non-Empty Cell";
    }

}
