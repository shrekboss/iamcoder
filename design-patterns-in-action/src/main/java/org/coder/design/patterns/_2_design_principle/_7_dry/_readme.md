## 实现逻辑重复

> 实现逻辑重复，但语义不重复

下面这样一段代码是否违反了 DRY 原则。如果违反了，你觉得应该如何重构，才能让它满足 DRY 原则？如果没有违反，那又是为什么呢？

```java
public class UserAuthenticator {
    public void authenticate(String username, String password) {
        if (!isValidUsername(username)) {
            // ...throw InvalidUsernameException...
        }
        if (!isValidPassword(password)) {
            // ...throw InvalidPasswordException...
        }
        //...省略其他代码...
    }

    private boolean isValidUsername(String username) {
        // check not null, not empty
        if (StringUtils.isBlank(username)) {
            return false;
        }
        // check length: 4~64
        int length = username.length();
        if (length < 4 || length > 64) {
            return false;
        }
        // contains only lowcase characters
        if (!StringUtils.isAllLowerCase(username)) {
            return false;
        }
        // contains only a~z,0~9,dot
        for (int i = 0; i < length; ++i) {
            char c = username.charAt(i);
            if (!(c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.') {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        // check not null, not empty
        if (StringUtils.isBlank(password)) {
            return false;
        }
        // check length: 4~64
        int length = password.length();
        if (length < 4 || length > 64) {
            return false;
        }
        // contains only lowcase characters
        if (!StringUtils.isAllLowerCase(password)) {
            return false;
        }
        // contains only a~z,0~9,dot
        for (int i = 0; i < length; ++i) {
            char c = password.charAt(i);
            if (!(c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.') {
                return false;
            }
        }
        return true;
    }
}
```

重构后，参考代码：

- [UserAuthenticator.java](UserAuthenticator.java)

经过重构之后，代码行数减少了，也没有重复的代码了，是不是更好了呢？答案是否定的。

- 单从名字上看，违反了“单一职责原则”和“接口隔离原则”
    - isValidUserName() 和 isValidPassword()
      两个函数，虽然从代码实现逻辑上看起来是重复的，但是从语义上并不重复。所谓“语义不重复”指的是：从功能上来看，这两个函数干的是完全不重复的两件事情，一个是校验用户名，另一个是校验密码。
- 如果按照第二种写法，将两个函数的合并，那就会存在潜在的问题。在未来的某一天，如果我们修改了密码的校验逻辑，就要把合并后的函数，重新拆成合并前的那两个函数。

## 功能语义重复

> 实现逻辑不重复，但语义重复，也就是功能重复，我们认为它违反了 DRY 原则

参考代码：

- [FunctionalSemanticRepetitionDemo.java](FunctionalSemanticRepetitionDemo.java)

## 代码执行重复

代码如下：

```java
public class UserService {
    private UserRepo userRepo;//通过依赖注入或者IOC框架注入

    public User login(String email, String password) {
        boolean existed = userRepo.checkIfUserExisted(email, password);
        if (!existed) {
            // ... throw AuthenticationFailureException...
        }
        User user = userRepo.getUserByEmail(email);
        return user;
    }
}

public class UserRepo {
    public boolean checkIfUserExisted(String email, String password) {
        if (!EmailValidation.validate(email)) {
            // ... throw InvalidEmailException...
        }

        if (!PasswordValidation.validate(password)) {
            // ... throw InvalidPasswordException...
        }

        //...query db to check if email&password exists...
    }

    public User getUserByEmail(String email) {
        if (!EmailValidation.validate(email)) {
            // ... throw InvalidEmailException...
        }
        //...query db to get user by email...
    }
}
```

- email 的校验逻辑被执行了两次，一次是在调用 checkIfUserExisted() 函数的时候，另一次是调用 getUserByEmail() 函数的时候。
- 代码中还有一处比较隐蔽的执行重复，不知道你发现了没有？实际上，login() 函数并不需要调用 checkIfUserExisted() 函数，只需要调用一次 getUserByEmail() 函数，从数据库中获取到用户的 email、password 等信息，然后跟用户输入的 email、password 信息做对比，依次判断是否登录成功。
- 实际上，这样的优化是很有必要的。因为 checkIfUserExisted() 函数和 getUserByEmail() 函数都需要查询数据库，而数据库这类的 I/O 操作是比较耗时的。我们在写代码的时候，应当尽量减少这类 I/O 操作。

重构后代码如下：
```java
public class UserService {
    private UserRepo userRepo;//通过依赖注入或者IOC框架注入

    public User login(String email, String password) {
        if (!EmailValidation.validate(email)) {
            // ... throw InvalidEmailException...
        }
        if (!PasswordValidation.validate(password)) {
            // ... throw InvalidPasswordException...
        }
        User user = userRepo.getUserByEmail(email);
        if (user == null || !password.equals(user.getPassword()) {
            // ... throw AuthenticationFailureException...
        }
        return user;
    }
}

public class UserRepo {
    public boolean checkIfUserExisted(String email, String password) {
        //...query db to check if email&password exists
    }

    public User getUserByEmail(String email) {
        //...query db to get user by email...
    }
}
```