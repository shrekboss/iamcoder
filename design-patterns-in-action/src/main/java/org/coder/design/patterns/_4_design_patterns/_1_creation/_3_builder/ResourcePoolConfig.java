package org.coder.design.patterns._4_design_patterns._1_creation._3_builder;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Getter
public class ResourcePoolConfig {

    private String name;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;

    private ResourcePoolConfig(Builder builder) {
        this.name = builder.name;
        this.maxTotal = builder.maxTotal;
        this.maxIdle = builder.maxIdle;
        this.minIdle = builder.minIdle;
    }

    /**
     * 将Builder类设计成了ResourcePoolConfig的内部类。也可以将Builder类设计成独立的非内部类ResourcePoolConfigBuilder。
     */
    public static class Builder {
        private static final int DEFAULT_MAX_TOTAL = 8;
        private static final int DEFAULT_MAX_IDLE = 8;
        private static final int DEFAULT_MIN_IDLE = 0;

        private String name;
        private int maxTotal = DEFAULT_MAX_TOTAL;
        private int maxIdle = DEFAULT_MAX_IDLE;
        private int minIdle = DEFAULT_MIN_IDLE;

        public ResourcePoolConfig build() {
            // 校验逻辑放到这里来做，包括必填项校验、依赖关系校验、约束条件校验等
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("...");
            }
            if (maxIdle > maxTotal) {
                throw new IllegalArgumentException("...");
            }
            if (minIdle > maxTotal || minIdle > maxIdle) {
                throw new IllegalArgumentException("...");
            }

            return new ResourcePoolConfig(this);
        }

        public Builder setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("...");
            }
            this.name = name;
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            if (maxTotal <= 0) {
                throw new IllegalArgumentException("...");
            }
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder setMaxIdle(int maxIdle) {
            if (maxIdle < 0) {
                throw new IllegalArgumentException("...");
            }
            this.maxIdle = maxIdle;
            return this;
        }

        public Builder setMinIdle(int minIdle) {
            if (minIdle < 0) {
                throw new IllegalArgumentException("...");
            }
            this.minIdle = minIdle;
            return this;
        }
    }

    @Override
    public String toString() {
        return "ResourcePoolConfig{" +
                "name='" + name + '\'' +
                ", maxTotal=" + maxTotal +
                ", maxIdle=" + maxIdle +
                ", minIdle=" + minIdle +
                '}';
    }

    public static void main(String[] args) {
        // 这段代码会抛出 IllegalArgumentException，因为 minIdle > maxIdle
        ResourcePoolConfig config = new ResourcePoolConfig.Builder()
                .setName("dbConnectionPool")
                .setMaxTotal(16)
                .setMaxIdle(10)
                .setMinIdle(12)
                .build();
        System.out.println(config);
    }
}
