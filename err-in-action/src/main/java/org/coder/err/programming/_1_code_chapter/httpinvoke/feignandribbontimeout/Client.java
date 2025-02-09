package org.coder.err.programming._1_code_chapter.httpinvoke.feignandribbontimeout;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "clientsdk")
public interface Client {

    @PostMapping("/feignandribbon/server")
    void server();
}
