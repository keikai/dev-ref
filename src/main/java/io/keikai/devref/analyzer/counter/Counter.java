package io.keikai.devref.analyzer.counter;

/**
 * A counter counts a specific item.
 * It follows "Chain of Responsibility" pattern.
 * @param <T>
 */
public abstract class Counter<T> {
    protected String name = "counter";
    protected int counter;
    protected Counter next;

    public Counter(Counter nextCounter) {
        this.next = nextCounter;
    }

    public void toNext(T object) {
        if (next != null) {
            next.process(object);
        }
    }

    public Counter getNext() {
        return next;
    }

    public void process(T object){
        counter++;
        toNext(object);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCounter() {
        return counter;
    }

}
