package org.coder.concurrency.programming.thread.communication.single.waitnotify;

import java.util.LinkedList;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class EventQueue {

    private final static int DEFAULT_MAX_EVENT = 10;
    private final int max;
    private final LinkedList<Event> eventQueue = new LinkedList<>();

    public EventQueue() {
        this(DEFAULT_MAX_EVENT);
    }

    public EventQueue(int max) {
        this.max = max;
    }

    private static void console(String message) {
        System.out.printf("%s:%s\n", Thread.currentThread().getName(), message);
    }

    public void offer(Event event) {
        synchronized (eventQueue) {
            if (eventQueue.size() >= max) {
                try {
                    console(" the queue is full.");
                    eventQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            console(" the new event is submitted.");
            eventQueue.addLast(event);
            eventQueue.notify();
        }
    }

    public Event take() {
        synchronized (eventQueue) {
            if (eventQueue.isEmpty()) {
                try {
                    console(" the queue is empty.");
                    // wait() 相当于 wait(0), 0 代表着永不超时
                    eventQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Event event = eventQueue.removeFirst();
            this.eventQueue.notify();
            console(" the event " + event + " is handled.");

            return event;
        }
    }

    static class Event {
    }
}
