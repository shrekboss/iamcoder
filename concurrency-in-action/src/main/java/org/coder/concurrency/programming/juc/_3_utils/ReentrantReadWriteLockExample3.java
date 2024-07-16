package org.coder.concurrency.programming.juc._3_utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * (2) 5个线程读和5个线程写的性能比较
 * 既然读写锁在高并发只读的情况下性能表现最差，那么在既有读又有写的并发情况下性能又会如何呢？
 * 我们基于3.6.3节中的基准测试代码稍作修改，如下所示。
 * 
 * 基准测试的结果显而易见，仍旧是读写锁的表现最差，我们将基准测试的每一个批次生成图形报告。
 * Benchmark                                               Mode  Cnt  Score   Error  Units
 * ReentrantReadWriteLockExample3.base                     avgt   10  0.029 ± 0.005  us/op
 * ReentrantReadWriteLockExample3.base:baseGet             avgt   10  0.054 ± 0.009  us/op
 * ReentrantReadWriteLockExample3.base:baseInc             avgt   10  0.004 ± 0.001  us/op
 * ReentrantReadWriteLockExample3.lock                     avgt   10  0.271 ± 0.007  us/op
 * ReentrantReadWriteLockExample3.lock:testLockGet         avgt   10  0.284 ± 0.023  us/op
 * ReentrantReadWriteLockExample3.lock:testLockInc         avgt   10  0.258 ± 0.011  us/op
 * ReentrantReadWriteLockExample3.rwlock                   avgt   10  0.738 ± 0.030  us/op
 * ReentrantReadWriteLockExample3.rwlock:testReadLockGet   avgt   10  1.199 ± 0.061  us/op
 * ReentrantReadWriteLockExample3.rwlock:testWriteLockInc  avgt   10  0.277 ± 0.002  us/op
 * ReentrantReadWriteLockExample3.sync                     avgt   10  0.387 ± 0.004  us/op
 * ReentrantReadWriteLockExample3.sync:testSyncGet         avgt   10  0.503 ± 0.008  us/op
 * ReentrantReadWriteLockExample3.sync:testSyncInc         avgt   10  0.271 ± 0.002  us/op
 * 
 * 3.7.4 读写锁总结
 * 读写锁提供了非常好的思路和解决方案，旨在提高某个时刻都为读操作的并发吞吐量，但是从基准测试的结果来看性能不尽如人意，
 * 因此在JDK1.8版本中引入了StampedLock的解决方案，3.9节中也将会继续介绍，另外推荐读者阅读一篇博客文章，
 * 也是关于读写锁性能对比的（该博客作者用语非常幽默，开头第一句话就让我会心一笑，敢情全世界的女婿都有相同的感受：
 * Synchronized sections are kind of like visiting your parents-in-law.
 * You want to be there as little as possible————同步就像你去看望你的岳父岳母，你希望尽可能地少去）。
 */
//基准测试的设定，10批次Warmup，10批次Measurement
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ReentrantReadWriteLockExample3 {

	@State(Scope.Group)
	public static class Test {
		private int x = 10;
		private final Lock lock = new ReentrantLock();
		//基准方法
		public int baseGet() {
			return x;
		}
		public void baseInc() {
			x++;
		}
		//使用lock进行方法同步
		public int lockGet() {
			lock.lock();
			try {
				return x;
			}finally {
				lock.unlock();
			}
		}
		
		public void lockInc() {
			lock.lock();
			try {
				x++;
			}finally {
				lock.unlock();
			}
		}
		//使用关键字synchronized进行方法同步
		public int syncGet() {
			synchronized(this) {
				return x;
			}
		}
		public void syncInc() {
			synchronized(this) {
				x++;
			}
		}
		
		private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private final Lock readLock = readWriteLock.readLock();
		private final Lock writeLock = readWriteLock.writeLock();
		
		public void writeLockInc() {
			writeLock.lock();
			try {
				x++;
			}finally {
				writeLock.unlock();
			}
		}
		public int readLockGet() {
			readLock.lock();
			try {
				return x;
			}finally {
				readLock.unlock();
			}
		}
	}
	
	//5个线程进行测试
	@GroupThreads(5)
	@Group("base")
	@Benchmark
	public void baseGet(Test test, Blackhole hole) {
		hole.consume(test.baseGet());
	}
	@GroupThreads(5)
	@Group("base")
	@Benchmark
	public void baseInc(Test test) {
		test.baseInc();
	}
	//5个线程进行测试
	@GroupThreads(5)
	@Group("lock")
	@Benchmark
	public void testLockGet(Test test, Blackhole hole) {
		hole.consume(test.lockGet());
	}
	@GroupThreads(5)
	@Group("lock")
	@Benchmark
	public void testLockInc(Test test, Blackhole hole) {
		test.lockInc();
	}
	//5个线程进行测试
	@GroupThreads(5)
	@Group("sync")
	@Benchmark
	public void testSyncGet(Test test, Blackhole hole) {
		hole.consume(test.syncGet());
	}
	@GroupThreads(5)
	@Group("sync")
	@Benchmark
	public void testSyncInc(Test test, Blackhole hole) {
		test.syncInc();
	}
	
	//5个线程进行测试
	@GroupThreads(5)
	@Group("rwlock")
	@Benchmark
	public void testReadLockGet(Test test, Blackhole hole) {
		hole.consume(test.readLockGet());
	}
	@GroupThreads(5)
	@Group("rwlock")
	@Benchmark
	public void testWriteLockInc(Test test, Blackhole hole) {
		test.writeLockInc();
	}
	public static void main(String[] args) throws RunnerException {
		Options opts = new OptionsBuilder()
				.include(ReentrantReadWriteLockExample3.class.getSimpleName())
				.forks(1)
				.build();
		new Runner(opts).run();
	}

}