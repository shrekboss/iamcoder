package org.coder.err.programming._1_code_chapter.httpinvoke.feignandribbontimeout;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.crayzer.err.coding.httpinvoke.feignandribbontimeout")
public class AutoConfig {
}
