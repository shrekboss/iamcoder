package org.coder.err.programming._1_code_chapter.transaction.nested;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SubUserService {

    @Autowired
    private UserDataMapper userDataMapper;

    //比较切换为 REQUIRES_NEW，这里的 createSubUser 可以插入数据成功
    @Transactional(propagation = Propagation.NESTED)
    public void createSubUser(String name) {
        userDataMapper.insert(name, "sub");
    }
}
