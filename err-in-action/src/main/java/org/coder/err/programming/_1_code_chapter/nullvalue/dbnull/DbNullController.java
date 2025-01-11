package org.coder.err.programming._1_code_chapter.nullvalue.dbnull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RestController
@RequestMapping("dbnull")
@Slf4j
public class DbNullController {

    @Resource
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        userRepository.save(new User());
    }

    @GetMapping("wrong")
    public void wrong() {
        log.info("result: {} {} {} ", userRepository.wrong1(), userRepository.wrong2(), userRepository.wrong3());
    }

    @GetMapping("right")
    public void right() {
        log.info("result: {} {} {} ", userRepository.right1(), userRepository.right2(), userRepository.right3());
    }
}
