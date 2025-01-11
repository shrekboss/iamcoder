package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._4_unique;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实现线程唯一的单例
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class IdGeneratorForClusterUnique {

    private AtomicLong id = new AtomicLong(0);

    private static final ConcurrentHashMap<Long, IdGeneratorForClusterUnique> instances = new ConcurrentHashMap<>();

    private IdGeneratorForClusterUnique() {
    }

    public static IdGeneratorForClusterUnique getInstance() {
        Long currentThreadId = Thread.currentThread().getId();
        instances.putIfAbsent(currentThreadId, new IdGeneratorForClusterUnique());
        return instances.get(currentThreadId);
    }

    public long getId() {
        return id.incrementAndGet();
    }

    public static void main(String[] args) {
        System.out.println(IdGeneratorForClusterUnique.getInstance().getId());
        System.out.println(IdGeneratorForClusterUnique.getInstance().getId());
        System.out.println(IdGeneratorForClusterUnique.getInstance().getId());
        System.out.println(IdGeneratorForClusterUnique.getInstance().getId());
    }
}
