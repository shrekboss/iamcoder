package org.coder.err.programming._1_code_chapter.logging.async;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequestMapping("async")
@RestController
public class LoggingController {

    /**
     * http://localhost:45678/async/log
     */
    @GetMapping("log")
    public void log() {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
    }

    /**
     * http://localhost:45678/async/manylog
     */
    @GetMapping("manylog")
    public void manylog(@RequestParam(name = "count", defaultValue = "1000") int count) {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, count).forEach(i -> log.info("log-{}", i));
        System.out.println("took " + (System.currentTimeMillis() - begin) + " ms");
    }

    /**
     * http://localhost:45678/async/performance
     */
    @GetMapping("performance")
    public void performance(@RequestParam(name = "count", defaultValue = "1000") int count) {
        long begin = System.currentTimeMillis();
        String payload = IntStream.rangeClosed(1, 1_000_000)
                .mapToObj(__ -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID();
        IntStream.rangeClosed(1, count).forEach(i -> log.info("{} {}", i, payload));
        Marker timeMarker = MarkerFactory.getMarker("time");
        log.info(timeMarker, "took {} ms", System.currentTimeMillis() - begin);
    }
}
