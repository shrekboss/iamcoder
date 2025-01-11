package org.coder.err.programming._1_code_chapter.transaction.transactionproxyfailed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("transactionproxyfailed")
@Slf4j
public class TransactionProxyFailedController {

    @Resource
    private UserService userService;

    /**
     * curl http://localhost:45678/transactionproxyfailed/wrong1?name=test
     */
    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name) {
        return userService.createUserWrong1(name);
    }

    /**
     * curl http://localhost:45678/transactionproxyfailed/wrong2?name=test
     */
    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name) {
        return userService.createUserWrong2(name);
    }

    /**
     * curl http://localhost:45678/transactionproxyfailed/wrong3?name=test
     */
    @GetMapping("wrong3")
    public int wrong3(@RequestParam("name") String name) {
        return userService.createUserWrong3(name);
    }

    /**
     * curl http://localhost:45678/transactionproxyfailed/right1?name=test
     */
    @GetMapping("right1")
    public int right1(@RequestParam("name") String name) {
        return userService.createUserRight(name);
    }

    /**
     * curl http://localhost:45678/transactionproxyfailed/right2?name=test
     */
    @GetMapping("right2")
    public int right2(@RequestParam("name") String name) {
        try {
            userService.createUserPublic(new UserEntity(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userService.getUserCount(name);
    }

}
