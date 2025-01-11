package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.HandlerClient;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.ProductCheckHandlerConfig;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.ProductVO;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.Result;
import org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck.handler.AbstractCheckHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
@RestController(value = "/product")
public class ProductController {

    /**
     * 使用Spring注入:所有继承了AbstractCheckHandler抽象类的Spring Bean都会注入进来。
     * Map的Key对应Bean的name,Value是name对应相应的Bean
     */
    @Resource
    private Map<String, AbstractCheckHandler> handlerMap;

    /**
     * 创建商品
     *
     * @return
     */
    @PostMapping(value = "/create")
    public Result createProduct(ProductVO param) {

        //参数校验，使用责任链模式
        Result paramCheckResult = this.paramCheck(param);
        if (!paramCheckResult.isSuccess()) {
            return paramCheckResult;
        }

        //创建商品
        return this.saveProduct(param);
    }

    private Result saveProduct(ProductVO param) {
        return Result.success();
    }

    /**
     * 参数校验：责任链模式
     *
     * @param param
     * @return
     */
    private Result paramCheck(ProductVO param) {

        //获取处理器配置：通常配置使用统一配置中心存储，支持动态变更
        ProductCheckHandlerConfig handlerConfig = this.getHandlerConfigFile();

        //获取处理器
        AbstractCheckHandler handler = this.getHandler(handlerConfig);

        //责任链：执行处理器链路
        Result executeChainResult = HandlerClient.executeChain(handler, param);
        if (!executeChainResult.isSuccess()) {
            System.out.println("创建商品 失败...");
            return executeChainResult;
        }

        //处理器链路全部成功
        return Result.success();
    }

    /**
     * 获取处理器配置：通常配置使用统一配置中心存储，支持动态变更
     *
     * @return
     */
    private ProductCheckHandlerConfig getHandlerConfigFile() {
        //配置中心存储的配置
        String configJson = "{\"handler\":\"nullValueCheckHandler\",\"down\":true,\"next\":{\"handler\":\"priceCheckHandler\",\"next\":{\"handler\":\"stockCheckHandler\",\"next\":null}}}";
        //转成Config对象
        ProductCheckHandlerConfig handlerConfig = JSON.parseObject(configJson, ProductCheckHandlerConfig.class);
        return handlerConfig;
    }

    /**
     * 获取处理器
     *
     * @param config
     * @return
     */
    private AbstractCheckHandler getHandler(ProductCheckHandlerConfig config) {
        //配置检查：没有配置处理器链路，则不执行校验逻辑
        if (Objects.isNull(config)) {
            return null;
        }
        //配置错误
        String handler = config.getHandler();
        if (StringUtils.isBlank(handler)) {
            return null;
        }
        //配置了不存在的处理器
        AbstractCheckHandler abstractCheckHandler = handlerMap.get(config.getHandler());
        if (Objects.isNull(abstractCheckHandler)) {
            return null;
        }

        //处理器设置配置Config
        abstractCheckHandler.setConfig(config);

        //递归设置链路处理器
        abstractCheckHandler.setNextHandler(this.getHandler(config.getNext()));

        return abstractCheckHandler;
    }
}
