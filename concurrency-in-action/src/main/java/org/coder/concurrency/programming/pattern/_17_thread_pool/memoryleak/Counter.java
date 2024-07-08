package org.coder.concurrency.programming.pattern._17_thread_pool.memoryleak;

public class Counter {
    private int i = 0;

    public int getAndIncrement() {
        return (i++);
    }
}