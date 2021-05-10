package io.keikai.devref.analyzer;

public class ReportItem {
    private String name;
    private int counter;

    public ReportItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
