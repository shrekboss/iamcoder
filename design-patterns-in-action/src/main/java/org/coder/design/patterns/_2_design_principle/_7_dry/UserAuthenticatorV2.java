package org.coder.design.patterns._2_design_principle._7_dry;

import org.apache.commons.lang3.StringUtils;

/**
 * 重构后的代码
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserAuthenticatorV2 {

    public void authenticate(String userName, String password) {
        if (!isValidUsernameOrPassword(userName)) {
            // ...throw InvalidUsernameException...
        }

        if (!isValidUsernameOrPassword(password)) {
            // ...throw InvalidPasswordException...
        }
    }

    private boolean isValidUsernameOrPassword(String usernameOrPassword) {
        // check not null, not empty
        if (StringUtils.isBlank(usernameOrPassword)) {
            return false;
        }

        // check length: 4~64
        int length = usernameOrPassword.length();
        if (length < 4 || length > 64) {
            return false;
        }
        // contains only lowcase characters
        if (!StringUtils.isAllLowerCase(usernameOrPassword)) {
            return false;
        }
        // contains only a~z,0~9,dot
        for (int i = 0; i < length; ++i) {
            char c = usernameOrPassword.charAt(i);
            if (!(c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.') {
                return false;
            }
        }

        return true;
    }
}
