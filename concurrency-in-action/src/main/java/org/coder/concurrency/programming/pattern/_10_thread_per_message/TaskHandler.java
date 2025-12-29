package org.coder.concurrency.programming.pattern._10_thread_per_message;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TaskHandler implements Runnable {
    private final Request request;

    public TaskHandler(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        System.out.println("Begin handle " + request);
        slowly();
        System.out.println("End handle " + request);
    }

    private void slowly() {
        try {
            TimeUnit.SECONDS.sleep(current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
