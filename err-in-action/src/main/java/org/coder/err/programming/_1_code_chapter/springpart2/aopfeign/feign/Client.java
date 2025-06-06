package org.coder.err.programming._1_code_chapter.springpart2.aopfeign.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "client")
public interface Client {
    @GetMapping("/feignaop/server")
    String api();
}
