package org.coder.err.programming._1_code_chapter.oom.impropermaxheadersize;

import lombok.extern.slf4j.Slf4j;
import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CommonMistakesApplicationGood {
    public static void main(String[] args) {
        Utils.loadPropertySource(CommonMistakesApplicationGood.class, "good.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplicationGood.class, args);
    }

}
