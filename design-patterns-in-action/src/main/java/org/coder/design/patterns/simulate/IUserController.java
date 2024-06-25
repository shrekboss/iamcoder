package org.coder.design.patterns.simulate;

import org.coder.design.patterns.simulate.vo.UserVo;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface IUserController {

    UserVo login(String telephone, String password);

    void register(UserVo user);

    UserVo register(String telephone, String password);
}
