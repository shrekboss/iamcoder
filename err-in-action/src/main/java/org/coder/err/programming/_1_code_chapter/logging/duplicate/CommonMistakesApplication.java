package org.coder.err.programming._1_code_chapter.logging.duplicate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplication {

    public static void main(String[] args) {
        /* 日志重复问题 */
        System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/loggerwrong.xml");
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/loggerright1.xml");
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/loggerright2.xml");

        /* 错误配置 LevelFilter 造成日志重复记录 */
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/filterwrong.xml");
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/filterright.xml");

        /* 自定义 filter */
        // System.setProperty("logging.config", "classpath:org/coder/err/programming/logging/duplicate/multiplelevelsfilter.xml");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

