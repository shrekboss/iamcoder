package org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CommonMistakesApplicationRetryClient {

    public static void main(String[] args) {
        System.setProperty("server.port", "45679");
        Utils.loadPropertySource(
                CommonMistakesApplicationRetryClient.class,
                "default-ribbon.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationRetryClient.class, args);
    }
}

