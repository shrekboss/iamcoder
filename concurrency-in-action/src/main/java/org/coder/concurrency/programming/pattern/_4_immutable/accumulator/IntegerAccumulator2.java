package org.coder.concurrency.programming.pattern._4_immutable.accumulator;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class IntegerAccumulator2 {

    private int init;

    public IntegerAccumulator2(int init) {
        this.init = init;
    }

    public int add(int i) {
        this.init += i;
        return this.init;
    }

    public int getValue() {
        return this.init;
    }

    public static void main(String[] args) {
        IntegerAccumulator2 accumulator = new IntegerAccumulator2(0);
        IntStream.range(0, 3).forEach(i -> new Thread(() -> {

            int inc = 0;
            while (true) {
                int oldValue;
                int result;

                // 如果单纯对 getValue 和 add 方法增加同步控制，虽然保证了单个方法的原子性，但是两个原子类型的操作在一起未必就是原子性的，
                // 因此在线程的逻辑执行单元中增加同步控制是最为合理的。
                synchronized (IntegerAccumulator2.class) {
                    oldValue = accumulator.getValue();
                    result = accumulator.add(inc);
                }
                System.out.println(oldValue + "+" + inc + "=" + result);
                if (inc + oldValue != result) {
                    System.err.println("ERROR:" + oldValue + "+" + inc + "=" + result);
                }
                inc++;
                slowly();
            }
        }).start());
    }

    private static void slowly() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
