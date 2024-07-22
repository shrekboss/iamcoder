package org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CommonMistakesApplicationNoRetryServer {

    public static void main(String[] args) {
        System.setProperty("server.port", "45678");
        System.setProperty("management.server.port", "12345");
        Utils.loadPropertySource(CommonMistakesApplicationNoRetryServer.class,"noretry-ribbon.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationNoRetryServer.class, args);
    }
}

