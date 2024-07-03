package org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm;

import java.util.concurrent.Callable;

public abstract class GuardedAction<V> implements Callable<V> {
    protected final Predicate guard;

    public GuardedAction(Predicate guard) {
        this.guard = guard;
    }
}