package org.coder.err.programming._1_code_chapter.transaction.transactionpropagation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("transactionpropagation")
@Slf4j
public class TransactionPropagationController {

    @Resource
    private UserService userService;

    /**
     * curl http://localhost:45678/transactionpropagation/wrong1?name=test
     */
    @GetMapping("wrong1")
    public int wrong(@RequestParam("name") String name) {
        try {
            userService.createUserWrong(new UserEntity(name));
        } catch (Exception ex) {
            log.error("createUserWrong failed, reason:{}", ex.getMessage());
        }
        return userService.getUserCount(name);
    }

    /**
     * curl http://localhost:45678/transactionpropagation/wrong2?name=test
     */
    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name) {
        try {
            userService.createUserWrong2(new UserEntity(name));
        } catch (Exception ex) {
            log.error("createUserWrong2 failed, reason:{}", ex.getMessage(), ex);
        }
        return userService.getUserCount(name);
    }

    /**
     * curl http://localhost:45678/transactionpropagation/right1?name=test
     */
    @GetMapping("right1")
    public int right(@RequestParam("name") String name) {
        userService.createUserRight(new UserEntity(name));
        return userService.getUserCount(name);
    }
}
