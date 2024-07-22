package org.coder.err.programming._1_code_chapter.transaction.transactionproxyfailed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableTransactionManagement
public class CommonMistakesApplication {

    public static void main(String[] args) {
        //System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, ".");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

