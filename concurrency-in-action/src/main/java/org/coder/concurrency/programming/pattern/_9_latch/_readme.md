## Latch 设计模式

> Latch(门阀) 设计模式提供了等待所有子任务完成，然后继续接下来工作的一种设计方法，自 JDK 1.5 起也提供了 CountDownLatch
> 的工具类，起作用与我们创建的并无两样，无论是我们开发的 CountDownLatch 还是 JDK 所提供的，当 await
> 超时的时候，已完成任务的线程自然正常结束，但是未完成的则不会被中断还会继续执行下去，也就是说 CountDownLatch
> 只提供了门阀的功能，并不负责对线程的管理控制，对线程的控制还需要程序员自己控制。
>
> Latch 的作用是为了等待所有子任务完成后再执行其他任务，因此可以对 Latch 进行再次扩展，增加回调接口用于运行所有子任务完成后的其他任务。

增加了回调功能的 CountDownLatch 代码如下：

```java
import org.coder.concurrency.programming.pattern._9_latch.WaitTimeoutException;

import java.util.concurrent.TimeUnit;

public CountDownLatch(int limit, Runnable runnable) {
  super(limit);
  this.runnable = runnable;
}

@Override
public void await() throws InterruptedException {
  synchronized (this) {
    while (limit > 0) {
      this.wait();
    }
  }

  if (null != runnable) {
    runnable.run();
  }
}

@Override
public void await(TimeUnit unit, long time) throws InterruptedException, WaitTimeoutException {

  if (time <= 0)
    throw new IllegalArgumentException("The time is invalid.");

  long remainingNanos = unit.toNanos(time);
  final long endNanos = System.nanoTime() + remainingNanos;

  synchronized (this) {
    while (limit > 0) {
      if (TimeUnit.NANOSECONDS.toMillis(remainingNanos) <= 0) {
        throw new WaitTimeoutException("The wait time over specify time.");
      }

      this.wait(TimeUnit.NANOSECONDS.toMillis(remainingNanos));
      remainingNanos = endNanos - System.nanoTime();
    }
  }

  if (null != runnable) {
    runnable.run();
  }
}
```

参考代码：

- [Latch.java](Latch.java)
    - [CountDownLatch.java](CountDownLatch.java)
- [WaitTimeoutException.java](WaitTimeoutException.java)
- [ProgrammerTravel.java](ProgrammerTravel.java)
