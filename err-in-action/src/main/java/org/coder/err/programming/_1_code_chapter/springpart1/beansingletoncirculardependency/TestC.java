package org.coder.err.programming._1_code_chapter.springpart1.beansingletoncirculardependency;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestC {
    @Getter
    private TestD testD;

    @Resource
    public TestC(@Lazy TestD testD) {
        this.testD = testD;
    }
}
