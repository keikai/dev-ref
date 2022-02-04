package io.keikai.devref.analyzer.counter;

import io.keikai.api.model.Sheet;

public class SheetCounter extends Counter<Sheet> {
    public SheetCounter(Counter processor) {
        super(processor);
        name ="Sheets";
    }
}
