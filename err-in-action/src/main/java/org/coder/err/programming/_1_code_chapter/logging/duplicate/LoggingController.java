package org.coder.err.programming._1_code_chapter.logging.duplicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("duplicate")
@RestController
public class LoggingController {

    /**
     * http://localhost:45678/duplicate/log
     */
    @GetMapping("log")
    public void log() {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
    }
}
