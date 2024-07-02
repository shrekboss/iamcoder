package org.coder.concurrency.programming.pattern._13_active_objects;

import java.util.LinkedList;

/**
 * (what) 主要作用于传送调用线程通过 Proxy 提交过来的 MethodMessage，但是这个传送带允许存放无限的 MethodMessage。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ActiveMessageQueue {

    private final LinkedList<ActiveMessage> messages = new LinkedList<>();

    public ActiveMessageQueue() {
        new ActiveDaemonThread(this).start();
    }

    public void offer(ActiveMessage activeMessage) {
        synchronized (this) {
            messages.addLast(activeMessage);
            this.notify();
        }
    }

    public ActiveMessage take() {
        synchronized (this) {
            while (messages.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return messages.removeFirst();
        }
    }
}
