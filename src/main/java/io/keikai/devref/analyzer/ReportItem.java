package io.keikai.devref.analyzer;

import io.keikai.devref.analyzer.counter.Counter;

public class ReportItem {
    private String name;
    private String result;

    public ReportItem(Counter counter){
        this.name = counter.getName();
        this.result = String.valueOf(counter.getCounter());
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
