package org.coder.design.patterns._3_patterns.creation.singleton.cases._1_resource_confic;

/**
 * Logger类的应用示例
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserController {
    public void login(String username, String password) {
        // ...省略业务逻辑代码...
        Logger.getInstance().log(username + " logined!");
    }
}