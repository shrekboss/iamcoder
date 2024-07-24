package org.coder.err.programming._1_code_chapter.logging.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplication {

    public static void main(String[] args) {
        // 同步 Appender /logging/performance
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/async/performance_sync.xml");

        // 异步 Appender /logging/performance
         System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/async/performance_async.xml");

        // /logging/manylog
//        System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/async/asyncwrong.xml");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

