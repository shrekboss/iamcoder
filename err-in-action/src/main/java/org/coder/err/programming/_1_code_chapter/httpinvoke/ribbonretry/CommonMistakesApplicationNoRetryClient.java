package org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CommonMistakesApplicationNoRetryClient {

    public static void main(String[] args) {
        System.setProperty("server.port", "45679");

        Utils.loadPropertySource(CommonMistakesApplicationNoRetryClient.class, "noretry-ribbon.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationNoRetryClient.class, args);
    }
}

