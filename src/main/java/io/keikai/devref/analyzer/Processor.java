package io.keikai.devref.analyzer;

public abstract class Processor<T> {
    protected Processor nextProcessor;
    protected ReportItem item;

    public Processor(Processor processor){
        nextProcessor = processor;
    }

    public void toNext(T cell){
        if (nextProcessor != null){
            nextProcessor.process(cell);
        }
    }

    public ReportItem getItem() {
        return item;
    }

    public Processor getNext() {
        return nextProcessor;
    }

    public abstract void process(T cell);

}
