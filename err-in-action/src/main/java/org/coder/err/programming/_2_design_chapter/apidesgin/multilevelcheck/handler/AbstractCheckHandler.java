package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.handler;

import lombok.Getter;
import lombok.Setter;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.ProductCheckHandlerConfig;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.ProductVO;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.Result;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 抽象类处理器
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
@Component
public abstract class AbstractCheckHandler {

    /**
     * 当前处理器持有下一个处理器的引用
     */
    @Getter
    @Setter
    protected AbstractCheckHandler nextHandler;


    /**
     * 处理器配置
     */
    @Setter
    @Getter
    protected ProductCheckHandlerConfig config;

    /**
     * 处理器执行方法
     *
     * @param param
     * @return
     */
    public abstract Result handle(ProductVO param);

    /**
     * 链路传递
     *
     * @param param
     * @return
     */
    protected Result next(ProductVO param) {
        //下一个链路没有处理器了，直接返回
        if (Objects.isNull(nextHandler)) {
            return Result.success();
        }

        //执行下一个处理器
        return nextHandler.handle(param);
    }
}
