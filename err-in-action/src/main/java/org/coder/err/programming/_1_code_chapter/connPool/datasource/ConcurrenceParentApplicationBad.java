package org.coder.err.programming._1_code_chapter.connPool.datasource;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConcurrenceParentApplicationBad {

    public static void main(String[] args) {
        /* connPool#datasource */
        Utils.loadPropertySource(ConcurrenceParentApplicationBad.class, "good.properties");
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(ConcurrenceParentApplicationBad.class, args);
    }

}
