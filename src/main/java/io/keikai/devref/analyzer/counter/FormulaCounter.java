package io.keikai.devref.analyzer.counter;

import io.keikai.model.SCell;

public class FormulaCounter extends Counter<SCell> {
    public FormulaCounter(Counter processor) {
        super(processor);
        name = "Formulas";
    }

    @Override
    public void process(SCell object) {
        if (object.getType() == SCell.CellType.FORMULA) {
            counter++;
        }
        toNext(object);
    }

}
