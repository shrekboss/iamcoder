package org.coder.err.programming._1_code_chapter.springpart2.aopfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.coder.err.programming._1_code_chapter.springpart2.aopfeign.feign")
public class Config {
}
