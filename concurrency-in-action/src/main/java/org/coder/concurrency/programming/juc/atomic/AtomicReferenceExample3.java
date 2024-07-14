package org.coder.concurrency.programming.juc.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 3.AtomicReference的非阻塞解决方案
 * 第2小节中的方案似乎满足了我们的需求，但是它却是一种阻塞式的解决方案，同一时刻只能有一个线程真正在工作，其他线程都将陷入阻塞，因此这并不是一种效率很高的解决方案，
 * 这个时候就可以利用AtomicReference的非阻塞原子性解决方案提供更加高效的方式了。
 * 基于AtomicReferenceExample1.java创建一个新的Java文件，并且用AtomicReference代替volatile关键字，代码如下所示。
 * 
 * 在下面的程序代码中，我们使用了AtomicReference封装DebitCard的对象引用，每一个对AtomicReference的更新操作，我们都采用CAS这一乐观非阻塞的方式进行，
 * 因此也会存在对DebitCard对象引用更改失败问题（更新时所持有的期望值引用有可能并不是AtomicReference所持有的当前引用，这也是第1小节中程序运行出现错误的根本原因。
 * 比如，A线程获得了DebitCard引用R1，在进行修改之前B线程已经将全局引用更新为R2，A线程仍然基于引用R1进行计算并且最终将全面引用更新为R1）。
 * 
 * CAS算法在此处就是要确保接下来要修改的对象引用是基于当前线程刚才获取的对象引用，否则更新将直接失败。运行下面的程序，我们再来分析一下控制台的输出。
 * 
 * 控制台的输出显示账号的金额按照10的步长在增长，由于非阻塞的缘故，数值20的输出有可能会出现在数值10的前面，数值130的输出则出现在了数值110的前面，但这并不妨碍amount的数值是按照10的步长增长的。
 *
 */
public class AtomicReferenceExample3 {
	//定义AtomicReference并且初始值为DebitCard("Alex", 0)
	private static AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(new DebitCard("Alex", 0));
	
	public static void main(String[] args) {
		//启动10个线程
		for(int i = 0; i < 10; i++) {
			new Thread("T-" + i) {
				@Override
				public void run() {
					for(int j = 0; j < 10; j++) {
						while(true) {
							//获取AtomicReference的当前值
							DebitCard dc = debitCardRef.get();
							//基于AtomicReference的当前值创建一个新的DebitCard
							DebitCard newDC = new DebitCard(dc.getAccount(), dc.getAmount() + 10);
							//基于CAS算法更新AtomicReference的当前值
							boolean flag = debitCardRef.compareAndSet(dc, newDC);
							if(flag) {
								System.out.println(newDC);
								break;
							}
						}
						
						try {
							TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}

}