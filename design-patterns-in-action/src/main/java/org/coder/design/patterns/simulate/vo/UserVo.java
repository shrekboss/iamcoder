package org.coder.design.patterns.simulate.vo;

import lombok.Data;

/**
 * 
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
