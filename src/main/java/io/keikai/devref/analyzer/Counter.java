package io.keikai.devref.analyzer;

/**
 * A counter counts a specific item and produce the result into a {@link ReportItem}
 * @param <T>
 */
public abstract class Counter<T> {
    protected Counter nextProcessor;
    protected ReportItem item;

    public Counter(Counter nextProcessor){
        this.nextProcessor = nextProcessor;
    }

    public void toNext(T object){
        if (nextProcessor != null){
            nextProcessor.process(object);
        }
    }

    public ReportItem getItem() {
        return item;
    }

    public Counter getNext() {
        return nextProcessor;
    }

    public abstract void process(T cell);

}
