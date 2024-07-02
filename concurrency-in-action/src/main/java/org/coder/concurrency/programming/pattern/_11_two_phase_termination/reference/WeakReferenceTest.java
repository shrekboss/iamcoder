package org.coder.concurrency.programming.pattern._11_two_phase_termination.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * (what) 无论是 SoftReference 还是 WeakReference 引用，被垃圾回收器回收后，都会被存放到与之关联的 ReferenceQueue 中。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class WeakReferenceTest {

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<Reference> queue = new ReferenceQueue<>();
        Reference ref = new Reference();
        WeakReference<Reference> reference = new WeakReference<>(ref, queue);
        ref = null;
        System.out.println("reference.get(): " + reference.get());

        // 手动执行 gc 操作
        System.gc();
        TimeUnit.SECONDS.sleep(1);

        // remove 方法是阻塞方法
        java.lang.ref.Reference<? extends Reference> gcedref = queue.remove();
        System.out.println("queue.remove(): " + gcedref);
    }
}
