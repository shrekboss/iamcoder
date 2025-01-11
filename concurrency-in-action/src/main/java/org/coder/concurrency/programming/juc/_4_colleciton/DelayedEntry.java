package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

//继承自Delayed接口
public class DelayedEntry implements Delayed {
    //元素数据内容
    private final String value;
    //用于计算失效时间
    private final long time;

    public DelayedEntry(String value, long delayTime) {
        this.value = value;
        //该元素可在(当前时间 + delayTime)毫秒后消费，也就是延迟消费delayTime毫秒
        this.time = delayTime + System.currentTimeMillis();
    }

    //重写compareTo方法，根据我们所实现的代码可以看出，队列头部的元素是最早即将失效的数据元素
    @Override
    public int compareTo(Delayed o) {
        if (this.time < ((DelayedEntry) o).time) {
            return -1;
        } else if (this.time > ((DelayedEntry) o).time) {
            return -1;
        } else {
            return 0;
        }
    }

    //重写getDelay方法，返回当前元素的延迟时间还剩余(remaining)多少个时间单位
    @Override
    public long getDelay(TimeUnit unit) {
        long delta = time - System.currentTimeMillis();
        return unit.convert(delta, TimeUnit.MILLISECONDS);
    }

    public String getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "DelayedEntry [value=" + value + ", time=" + time + "]";
    }
}