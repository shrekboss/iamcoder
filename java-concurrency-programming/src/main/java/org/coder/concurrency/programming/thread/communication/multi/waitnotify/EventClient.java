package org.coder.concurrency.programming.thread.communication.multi.waitnotify;

import java.util.concurrent.TimeUnit;

/**
 * 多个 Producer 线程和多个 Consumer 线程，多线程间的通信
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class EventClient {

    public static void main(String[] args) {
        final EventQueue eventQueue = new EventQueue();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (; ; ) {
                    eventQueue.offer(new EventQueue.Event());
                }
            }, "Produce-" + i).start();
        }

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                for (; ; ) {
                    eventQueue.take();
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Consumer-" + i).start();
        }



    }
}
