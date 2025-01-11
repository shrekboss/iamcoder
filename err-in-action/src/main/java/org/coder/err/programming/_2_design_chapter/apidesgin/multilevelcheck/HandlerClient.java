package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck;

import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.handler.AbstractCheckHandler;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class HandlerClient {

    public static Result executeChain(AbstractCheckHandler handler, ProductVO param) {
        //执行处理器
        Result handlerResult = handler.handle(param);
        if (!handlerResult.isSuccess()) {
            System.out.println("HandlerClient 责任链执行失败返回：" + handlerResult);
            return handlerResult;
        }
        return Result.success();
    }

    public static void main(String[] args) {

    }
}
