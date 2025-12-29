package org.coder.concurrency.programming.pattern._12_worker_thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Test {

    public static void main(String[] args) {
        final ProductionChannel channel = new ProductionChannel(5);
        AtomicInteger productionNo = new AtomicInteger();
        IntStream.range(1, 8).forEach(i ->
                new Thread(() ->
                {
                    while (true) {
                        channel.offerProduction(new Production(productionNo.getAndIncrement()));
                        try {
                            TimeUnit.SECONDS.sleep(current().nextInt(10));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start()
        );
    }
}
