package org.coder.design.patterns._3_design_patterns.creation._1_singleton.cases._2_globally_unique;

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
