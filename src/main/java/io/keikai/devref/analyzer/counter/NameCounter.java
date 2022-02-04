package io.keikai.devref.analyzer.counter;

import io.keikai.api.Ranges;
import io.keikai.api.model.Sheet;

public class NameCounter extends Counter<Sheet> {
    public NameCounter(Counter processor) {
        super(processor);
        name = "Named Range";
    }

    @Override
    public void process(Sheet object) {
        counter += Ranges.getNames(object).size();
        toNext(object);
    }

}
