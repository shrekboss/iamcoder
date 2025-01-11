package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 处理器配置类
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class ProductCheckHandlerConfig {

    /**
     * 处理器Bean名称
     */
    private String handler;
    /**
     * 下一个处理器
     */
    private ProductCheckHandlerConfig next;
    /**
     * 是否降级
     */
    private Boolean down = Boolean.FALSE;
}
