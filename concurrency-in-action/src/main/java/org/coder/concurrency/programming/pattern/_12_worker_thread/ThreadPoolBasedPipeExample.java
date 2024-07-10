package org.coder.concurrency.programming.pattern._12_worker_thread;

import org.coder.concurrency.programming.pattern._12_worker_thread._reusable.AbstractPipe;
import org.coder.concurrency.programming.pattern._12_worker_thread._reusable.Pipe;
import org.coder.concurrency.programming.pattern._12_worker_thread._reusable.PipeException;
import org.coder.concurrency.programming.pattern._12_worker_thread._reusable.SimplePipeline;
import org.coder.concurrency.programming.util.Debug;
import org.coder.concurrency.programming.util.Tools;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBasedPipeExample {

    public static void main(String[] args) {
        final ThreadPoolExecutor executorService =
                new ThreadPoolExecutor(1, Runtime.getRuntime()
                        .availableProcessors() * 2, 60, TimeUnit.MINUTES,
                        new SynchronousQueue<>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final SimplePipeline<String, String> pipeline = new SimplePipeline<>();
        Pipe<String, String> pipe = new AbstractPipe<String, String>() {

            @Override
            protected String doProcess(String input) throws PipeException {
                String result =
                        input + "->[pipe1," + Thread.currentThread().getName()
                                + "]";
                Debug.info(result);
                return result;
            }
        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        pipe = new AbstractPipe<String, String>() {

            @Override
            protected String doProcess(String input) throws PipeException {
                String result =
                        input + "->[pipe2," + Thread.currentThread().getName()
                                + "]";
                Debug.info(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }
        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        pipe = new AbstractPipe<String, String>() {

            @Override
            protected String doProcess(String input) throws PipeException {
                String result =
                        input + "->[pipe3," + Thread.currentThread().getName()
                                + "]";
                Debug.info(result);

                // 模拟实际操作的耗时
                Tools.randomPause(200, 90);
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

                // 在最后一个Pipe中关闭线程池
                executorService.shutdown();
                try {
                    executorService.awaitTermination(timeout, unit);
                } catch (InterruptedException e) {
                    ;
                }
            }
        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        pipeline.init(pipeline.newDefaultPipelineContext());

        int N = 100;
        try {
            for (int i = 0; i < N; i++) {
                pipeline.process("Task-" + i);
            }
        } catch (IllegalStateException e) {
            ;
        } catch (InterruptedException e) {
            ;
        }

        pipeline.shutdown(10, TimeUnit.SECONDS);

    }

}