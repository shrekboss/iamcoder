package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._2_globally_unique;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 枚举式 Id 生成器
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public enum IdGeneratorEnum {
    INSTANCE;
    private final AtomicLong id = new AtomicLong(0);

    public long getId() {
        return INSTANCE.id.incrementAndGet();
    }
}
