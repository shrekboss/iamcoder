package org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(basePackages = "org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry.feign")
public class AutoConfig {
}
