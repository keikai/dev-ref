package io.keikai.devref.analyzer;

import io.keikai.model.SCell;

public class FormulaProcessor extends Processor<SCell>{
    public FormulaProcessor(Processor processor) {
        super(processor);
        item = new ReportItem("Formulas");
    }

    @Override
    public void process(SCell cell) {
        if (cell.getType() == SCell.CellType.FORMULA) {
            item.setCounter(item.getCounter() + 1);
        }
        toNext(cell);
    }

}
