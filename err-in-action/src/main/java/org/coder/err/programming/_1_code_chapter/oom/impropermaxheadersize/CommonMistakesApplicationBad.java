package org.coder.err.programming._1_code_chapter.oom.impropermaxheadersize;

import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonMistakesApplicationBad {
    public static void main(String[] args) {
        Utils.loadPropertySource(CommonMistakesApplicationBad.class, "bad.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationBad.class, args);
    }
}

