package org.coder.design.patterns._3_design_patterns.creation._1_singleton.cases._4_unique;

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

    private IdGeneratorForClusterUnique() {}

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
