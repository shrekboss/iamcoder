package org.coder.concurrency.programming.pattern._16_producer_consumer;

import java.util.concurrent.BlockingDeque;

public interface WorkStealingEnabledChannel<P> extends Channel<P> {
    P take(BlockingDeque<P> preferredQueue) throws InterruptedException;
}