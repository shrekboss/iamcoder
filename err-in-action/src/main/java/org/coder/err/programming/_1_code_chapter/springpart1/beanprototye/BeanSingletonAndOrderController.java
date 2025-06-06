package org.coder.err.programming._1_code_chapter.springpart1.beanprototye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("beansingletonandorder")
public class BeanSingletonAndOrderController {

    // Spring 注入的 SayService 的 List，第一个元素是 SayBye，第二个元素是 SayHello， 存在顺序问题
    @Resource
    List<SayService> sayServiceList;
    @Resource
    private ApplicationContext applicationContext;

    /**
     * http://localhost:45678/beansingletonandorder/test
     */
    @GetMapping("test")
    public void test() {
        log.info("====================");
        // 第一次调用和第二次调用的时候，SayBye 对象都是 4c0bfe9e，SayHello 也是一样的问题
        // 解决 @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
        sayServiceList.forEach(SayService::say);
    }

    /**
     * http://localhost:45678/beansingletonandorder/test2
     */
    @GetMapping("test2")
    public void test2() {
        log.info("====================");
        // 如果不希望走代理的话还有一种方式是，每次直接从 ApplicationContext 中获取 Bean
        // @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        applicationContext.getBeansOfType(SayService.class).values().forEach(SayService::say);
    }
}
