package org.coder.design.patterns.common.vo;

import lombok.Data;

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
@Data
public class UserVo {

    private String telephone, password;

    public UserVo(String telephone, String password) {

        this.telephone = telephone;
        this.password = password;
    }
}
