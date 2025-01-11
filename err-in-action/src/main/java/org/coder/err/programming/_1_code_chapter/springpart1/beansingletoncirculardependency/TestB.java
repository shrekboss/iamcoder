package org.coder.err.programming._1_code_chapter.springpart1.beansingletoncirculardependency;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestB {
    @Resource
    @Getter
    private TestA testA;
}
