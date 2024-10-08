package org.coder.concurrency.programming.pattern._11_two_phase_termination.reference;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SocketCleaningTracker {
    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    static {
        new Cleaner().start();
    }

    public static void track(Socket socket) {
        new Tracker(socket, queue);
    }

    private static class Cleaner extends Thread {
        private Cleaner() {
            super("SocketCleaningTracker");
            setDaemon(true);
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Tracker tracker = (Tracker) queue.remove();
                    tracker.close();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static final class Tracker extends PhantomReference<Object> {
        private final Socket socket;

        Tracker(Socket socket, ReferenceQueue<? super Object> queue) {
            super(socket, queue);
            this.socket = socket;
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
