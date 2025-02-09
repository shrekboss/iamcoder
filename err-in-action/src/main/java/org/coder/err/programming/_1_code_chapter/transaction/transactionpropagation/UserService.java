package org.coder.err.programming._1_code_chapter.transaction.transactionpropagation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
public class UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private SubUserService subUserService;

    @Transactional
    public void createUserWrong(UserEntity entity) {
        createMainUser(entity);
        subUserService.createSubUserWithExceptionWrong(entity);
    }

    public int getUserCount(String name) {
        return userRepository.findByName(name).size();
    }

    //  - createUserWrong2 failed, reason:Transaction silently rolled back because it has been marked as rollback-only
    @Transactional
    public void createUserWrong2(UserEntity entity) {
        createMainUser(entity);
        try {
            subUserService.createSubUserWithExceptionWrong(entity);
        } catch (Exception ex) {
            // 虽然捕获了异常，但是因为没有开启新事务，而当前事务因为异常已经被标记为rollback了，所
            // 以最终还是会回滚。
            log.error("create sub user error:{}", ex.getMessage());
        }
    }

    @Transactional
    public void createUserRight(UserEntity entity) {
        createMainUser(entity);
        try {
            subUserService.createSubUserWithExceptionRight(entity);
        } catch (Exception ex) {
            // 捕获异常，防止主方法回滚
            log.error("create sub user error:{}", ex.getMessage());
        }
    }

    private void createMainUser(UserEntity entity) {
        userRepository.save(entity);
        log.info("createMainUser finish");
    }
}
