package org.coder.err.programming._1_code_chapter.transaction.nested;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
public class UserService {

    @Resource
    private UserDataMapper userDataMapper;

    @Resource
    private SubUserService subUserService;

    @Transactional
    public void createUser(String name) {
        createMainUser(name);
        try {
            subUserService.createSubUser(name);
        } catch (Exception ex) {
            log.error("create sub user error:{}", ex.getMessage());
        }
        //如果 createSubUser 是 NESTED 模式，这里抛出异常会导致嵌套事务无法『提交』
        throw new RuntimeException("create main user error");
    }

    private void createMainUser(String name) {
        userDataMapper.insert(name, "main");
    }


    public int getUserCount(String name) {
        return userDataMapper.count(name);
    }
}
