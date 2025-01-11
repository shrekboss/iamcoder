package org.coder.err.programming._1_code_chapter.springpart1.beansingletoncirculardependency;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestD {
    @Getter
    private TestC testC;

    @Resource
    public TestD(TestC testC) {
        this.testC = testC;
    }
}
