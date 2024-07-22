package org.coder.err.programming._1_code_chapter.httpinvoke.feignandribbontimeout;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplicationTimeoutByBoth {

    public static void main(String[] args) {
        Utils.loadPropertySource(FeignAndRibbonController.class, "default.properties");
        Utils.loadPropertySource(FeignAndRibbonController.class, "feign.properties");
        Utils.loadPropertySource(FeignAndRibbonController.class, "ribbon.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationTimeoutByBoth.class, args);
    }
}

