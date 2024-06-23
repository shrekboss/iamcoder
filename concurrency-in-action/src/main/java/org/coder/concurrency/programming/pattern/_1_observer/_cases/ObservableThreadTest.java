package org.coder.concurrency.programming.pattern._1_observer._cases;

import java.util.concurrent.TimeUnit;

/**
 * 测试运行
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ObservableThreadTest {

    public static void main(String[] args) {

        final TaskLifecycle<String> lifecycle = new TaskLifecycle.EmptyLifecycle<String>() {
            @Override
            public void onStart(Thread thread) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRunning(Thread thread) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(Thread thread, String result) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Thread thread, Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        final Observable observableThread = new ObservableThread<>(lifecycle, () -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" finished done.");
            return "Hello Observer";
        });

        observableThread.start();
    }

    /**
     * 和平时使用 Thread并没有太大的区别
     */
//    public static void main(String[] args) {
//
//        Observable observableThread = new ObservableThread<>(() ->
//        {
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(" finished done.");
//            return null;
//        });
//
//        observableThread.start();
//    }
}
