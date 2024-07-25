package org.coder.err.programming._1_code_chapter.serialization.deserializationconstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplication {

    public static void main(String[] args) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }
}

