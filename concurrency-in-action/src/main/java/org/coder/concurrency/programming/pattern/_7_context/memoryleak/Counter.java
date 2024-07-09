package org.coder.concurrency.programming.pattern._7_context.memoryleak;

public class Counter {
    private int i = 0;

    public int getAndIncrement() {
        return (i++);
    }
}