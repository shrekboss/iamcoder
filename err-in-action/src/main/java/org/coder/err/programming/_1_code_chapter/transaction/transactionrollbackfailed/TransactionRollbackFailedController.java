package org.coder.err.programming._1_code_chapter.transaction.transactionrollbackfailed;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("transactionrollbackfailed")
@Slf4j
public class TransactionRollbackFailedController {

    @Resource
    private UserService userService;

    /**
     * curl http://localhost:45678/transactionrollbackfailed/wrong1?name=test
     */
    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name) {
        userService.createUserWrong1(name);
        return userService.getUserCount(name);
    }

    /**
     * curl http://localhost:45678/transactionrollbackfailed/wrong2?name=test
     */
    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name) {
        try {
            userService.createUserWrong2(name);
        } catch (Exception e) {
            log.error("create user failed", e);
        }
        return userService.getUserCount(name);
    }

    /**
     * curl http://localhost:45678/transactionrollbackfailed/right1?name=test
     */
    @GetMapping("right1")
    public int right1(@RequestParam("name") String name) {
        try {
            userService.createUserRight1(name);
        } catch (Exception e) {
            log.error("create user failed", e);
        }
        return userService.getUserCount(name);
    }

    /**
     * curl http://localhost:45678/transactionrollbackfailed/right2?name=test
     */
    @GetMapping("right2")
    public int right2(@RequestParam("name") String name) {
        try {
            userService.createUserRight2(name);
        } catch (Exception e) {
            log.error("create user failed", e);
        }
        return userService.getUserCount(name);
    }
}
