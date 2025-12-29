package org.coder.concurrency.programming.pattern._5_future;

import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class FutureTest {

    public static void main(String[] args) throws InterruptedException {
        FutureService<String, Integer> service = FutureService.newService();

        Future<?> future = service.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("I am finish done!");
        });

        // get 方法会使当前线程进入阻塞
        System.out.println("future.get(): " + future.get());

        Future<Integer> futureResult = service.submit(input -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return input.length();
        }, "Hello", System.out::println);

        // 也可以通过一下方式获取结果
        System.out.println(futureResult.get());
    }
}
