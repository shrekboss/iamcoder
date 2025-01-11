package org.coder.err.programming._1_code_chapter.transaction.transactionpropagation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
public class SubUserService {

    @Resource
    private UserRepository userRepository;

    @Transactional
    public void createSubUserWithExceptionWrong(UserEntity entity) {
        log.info("createSubUserWithExceptionWrong start");
        userRepository.save(entity);
        throw new RuntimeException("invalid status");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSubUserWithExceptionRight(UserEntity entity) {
        log.info("createSubUserWithExceptionRight start");
        userRepository.save(entity);
        throw new RuntimeException("invalid status");
    }
}
