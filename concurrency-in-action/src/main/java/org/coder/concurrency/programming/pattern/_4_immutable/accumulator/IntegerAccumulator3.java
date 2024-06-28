package org.coder.concurrency.programming.pattern._4_immutable.accumulator;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
public final class IntegerAccumulator3 {

    private final int init;

    public IntegerAccumulator3(int init) {
        this.init = init;
    }

    /**
     * 构造新的累加器，需要用到另外一个 accumulator 和初始值
     */
    public IntegerAccumulator3(IntegerAccumulator3 accumulator, int init) {
        this.init = accumulator.getValue() + init;
    }

    /**
     * 每次相加都会产生一个新的 IntegerAccumulator
     */
    public IntegerAccumulator3 add(int i) {
        return new IntegerAccumulator3(this, i);
    }

    public int getValue() {
        return this.init;
    }

    public static void main(String[] args) {
        IntegerAccumulator3 accumulator = new IntegerAccumulator3(0);
        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            int inc = 0;
            while (true) {
                int oldValue = accumulator.getValue();
                int result = accumulator.add(inc).getValue();
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
