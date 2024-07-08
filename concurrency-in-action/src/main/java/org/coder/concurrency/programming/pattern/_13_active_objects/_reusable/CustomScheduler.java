package org.coder.concurrency.programming.pattern._13_active_objects._reusable;

import java.util.concurrent.*;

public class CustomScheduler implements Runnable {
    final LinkedBlockingQueue<Runnable> activationQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        dispatch();
    }

    public <T> Future<T> enqueue(Callable<T> methodRequest) {
        final FutureTask<T> task = new FutureTask<T>(methodRequest) {

            @Override
            public void run() {
                try {
                    super.run();
                    // 捕获所以可能抛出的对象，避免该任务运行失败而导致其所在的线程终止。
                } catch (Throwable t) {
                    this.setException(t);
                }
            }

        };

        try {
            activationQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return task;
    }

    public void dispatch() {
        while (true) {
            Runnable methodRequest;
            try {
                methodRequest = activationQueue.take();

                // 防止个别任务执行失败导致线程终止的代码在run方法中
                methodRequest.run();
            } catch (InterruptedException e) {
                // 处理该异常
            }

        }
    }

    public static void main(String[] args) {

        CustomScheduler scheduler = new CustomScheduler();
        Thread t = new Thread(scheduler);
        t.start();
        Future<String> result = scheduler.enqueue(new Callable<String>() {

            @Override
            public String call() throws Exception {
                Thread.sleep(1500);
                int i = 1;
                if (1 == i) {
                    throw new RuntimeException("test");
                }
                return "ok";
            }

        });

        try {
            System.out.println(result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}