package org.coder.concurrency.programming.pattern._7_context;

public class ThreadPrivateMember {

    public static void main(String[] args) throws InterruptedException {
        XThread thread;
        for (int i = 0; i < 3; i++) {
            thread = new XThread("message-" + i);
            thread.start();
        }
        Thread.sleep(50);
    }

    private static class XThread extends Thread {
        private final String message;

        public XThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println(message);
            }
        }
    }
}