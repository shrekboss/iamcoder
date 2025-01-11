package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
@SpringBootApplication
public class MultiLevelCheckApplication {
    public static void main(String[] args) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(MultiLevelCheckApplication.class, args);
    }
}