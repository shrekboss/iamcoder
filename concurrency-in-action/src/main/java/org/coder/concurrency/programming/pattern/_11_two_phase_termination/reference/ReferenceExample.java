package org.coder.concurrency.programming.pattern._11_two_phase_termination.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.TimeUnit;

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
public class ReferenceExample {
    public static void main(String[] args) throws InterruptedException {

        // LRUCache 的基本测试
        basicTest();
        // Strong Reference 的测试
        StrongReferenceTest();
        // Soft Reference 的测试
        SoftReferenceTest();
        // Phantom Reference 的测试
        PhantomReferenceTest();
    }

    private static void PhantomReferenceTest() throws InterruptedException {
        ReferenceQueue<Reference> queue = new ReferenceQueue<>();
        PhantomReference<Reference> reference = new PhantomReference<>(new Reference(), queue);
        System.out.println(reference.get());//始终返回null
        System.gc();
        java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
        System.out.println(gcedRef);
    }

    private static void SoftReferenceTest() throws InterruptedException {
        SoftLRUCache<Integer, Reference> cache = new SoftLRUCache<>(1000, key -> new Reference());
        System.out.println(cache);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            cache.get(i);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("The " + i + " reference stored at cache.");
        }
    }

    private static void StrongReferenceTest() throws InterruptedException {
        LRUCache<Integer, Reference> cache = new LRUCache<>(200, key -> new Reference());
        System.out.println(cache);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            cache.get(i);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("The " + i + " reference stored at cache.");
        }
    }

    public static void basicTest() {
        LRUCache<String, Reference> cache = new LRUCache<>(5, key -> new Reference());
        cache.get("Alex");
        cache.get("Jack");
        cache.get("Gavin");
        cache.get("Dillon");
        cache.get("Leo");

        cache.get("Jenny");
        System.out.println(cache.toString());
    }
}

