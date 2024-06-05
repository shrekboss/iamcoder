package org.coder.concurrency.programming.thread.api;

import java.util.stream.IntStream;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadYield {

    public static void main(String[] args) {
        IntStream.range(0, 2).mapToObj(ThreadYield::create).forEach(Thread::start);
    }

    private static Thread create(int index) {
        return new Thread(
                () -> {
                    // 如果注释部分打开，顺序永远是 0 1，否则顺序可能是 1 0 或这 0 1
                    // 注释部分 - start
//                    if (index == 0) {
//                        Thread.yield();
//                    }
                    // 注释部分 - end
                    System.out.println(index);
                }
        );
    }
}
