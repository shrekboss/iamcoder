package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.4.2使用Semaphore定义try lock
 * 无论是synchronized关键字，还是笔者在《Java高并发编程详解：多线程与架构设计》一书中定义的若干显式锁，都存在一个问题，
 * 那就是，当某个时刻获取不到锁的时候，当前线程会进入阻塞状态。这种状态有些时候并不是我们所期望的，如果获取不到锁线程还可以进行其他操作，
 * 而不一定非得将其阻塞（事实上，Lock接口中就提供了try lock的方法，当某个线程获取不到对共享资源执行的权限时将会立即返回，
 * 而不是使当前线程进入阻塞状态），本节将借助Semaphore提供的方法实现一个显式锁，该锁的主要作用是try锁，若获取不到锁就会立即返回。
 * 
 * 下面的代码非常简单，其核心思想是借助于只有一个许可证的Semaphore进行tryAcquire的操作，运行代码我们可以看到如下的结果，
 * 没有抢到锁的线程也会立即返回，并且不会导致当前线程进入阻塞状态中。
 */
public class SemaphoreExample2 {

	public static void main(String[] args) {
		final TryLock tryLock = new TryLock();
		//启动一个线程，尝试获取tryLock，如果获取不成功则将进行其他的操作，该线程不用进入阻塞状态
		new Thread(() -> {
			boolean gotLock = tryLock.tryLock();
			if(!gotLock) {
				System.out.println(Thread.currentThread() + "can't get the lock, will do other thing.");
				return;
			}
			try {
				simulateWork();
			}finally {
				tryLock.unlock();
			}
		}).start();
		//main线程也会参与tryLock的争抢，同样，如果抢不到tryLock，则main线程不会进入阻塞状态
		boolean gotLock = tryLock.tryLock();
		if(!gotLock) {
			System.out.println(Thread.currentThread() + "can't get the lock, will do other thing.");
		}else {
			try {
				simulateWork();
			}finally {
				tryLock.unlock();
			}
		}
	}
	//定义TryLock类
	private static class TryLock {
		//定义permit为1的semaphore
		private final Semaphore semaphore = new Semaphore(1);
		
		public boolean tryLock() {
			return semaphore.tryAcquire();
		}
		
		public void unlock() {
			semaphore.release();
			System.out.println(Thread.currentThread() + " release lock");
		}
	}
	//随机休眠
	private static void simulateWork() {
		try {
			System.out.println(Thread.currentThread() + " get the lock and do working...");
			TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}