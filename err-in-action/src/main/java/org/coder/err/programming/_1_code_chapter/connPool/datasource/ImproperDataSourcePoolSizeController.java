package org.coder.err.programming._1_code_chapter.connPool.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/improperdatasourcepoolsize")
@Slf4j
public class ImproperDataSourcePoolSizeController {
    @Autowired
    private UserService userService;

    /**
     * http://localhost:45678/improperdatasourcepoolsize/test
     */
    @GetMapping("/test")
    public Object test() {
        return userService.register();
    }
}