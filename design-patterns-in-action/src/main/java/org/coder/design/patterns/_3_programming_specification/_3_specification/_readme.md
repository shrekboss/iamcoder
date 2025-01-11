## 利用上下文简化命名

类属性可以借助类这个上下文来简化命名

```java
public class User {
    private String userName;
    private String userPassword;
    private String userAvatarUrl;
    //...

    public static void main(String[] args) {
        User user = new User();
        user.getName(); // 借助user对象这个上下文
    }
}
```

函数参数可以借助函数这个上下文来简化命名

```java
public void uploadUserAvatarImageToAliyun(String userAvatarImageUri);

//利用上下文简化为：
public void uploadUserAvatarImageToAliyun(String imageUri);
```

## 注释到底该写什么？

类注释

```java
/**
 * (what) Bean factory to create beans. 
 *
 * (why) The class likes Spring IOC framework, but is more lightweight. 
 *
 * (how) Create objects from different sources sequentially:
 * user specified object > SPI > configuration > default object.
 */
public class BeansFactory {
    // ...
}
```

对于逻辑比较复杂的代码或者比较长的函数，如果不好提炼、不好拆分成小的函数调用，那我们可以借助总结性的注释来让代码结构更清晰、更有条理。

```java
import org.apache.commons.lang3.StringUtils;

public boolean isValidPasword(String password) {
    // check if password is null or empty
    if (StringUtils.isBlank(password)) {
        return false;
    }

    // check if the length of password is between 4 and 64
    int length = password.length();
    if (length < 4 || length > 64) {
        return false;
    }

    // check if password contains only a~z,0~9,dot
    for (int i = 0; i < length; ++i) {
        char c = password.charAt(i);
        if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.')) {
            return false;
        }
    }
    return true;
}
```

## 把代码分割成更小的单元块

```java
import java.util.Calendar;// 重构前的代码
public void invest(long userId, long financialProductId) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
    if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
        return;
    }
    //...
}

// 重构后的代码：提炼函数之后逻辑更加清晰
public void invest(long userId, long financialProductId) {
    if (isLastDayOfMonth(new Date())) {
        return;
    }
    //...
}

public boolean isLastDayOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
    if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
        return true;
    }
    return false;
}
```

## 避免函数参数过多

考虑函数是否职责单一，是否能通过拆分成多个函数的方式来减少参数。

```java
public User getUser(String username, String telephone, String email);

// 拆分成多个函数
public User getUserByUsername(String username);
public User getUserByTelephone(String telephone);
public User getUserByEmail(String email);
```

将函数的参数封装成对象。

```java
public void postBlog(String title, String summary, String keywords, String content, String category, long authorId);

// 将参数封装成对象
public class Blog {
    private String title;
    private String summary;
    private String keywords;
    private Strint content;
    private String category;
    private long authorId;
}

public void postBlog(Blog blog);
```

## 勿用函数参数来控制逻辑

不要在函数中使用布尔类型的标识参数来控制内部逻辑，true 的时候走这块逻辑，false
的时候走另一块逻辑。这明显违背了单一职责原则和接口隔离原则。建议将其拆成两个函数，可读性上也要更好。

```java
public void buyCourse(long userId, long courseId, boolean isVip);

// 将其拆分成两个函数
public void buyCourse(long userId, long courseId);
public void buyCourseForVip(long userId, long courseId);
```

如果函数是 private 私有函数，影响范围有限，或者拆分之后的两个函数经常同时被调用，可以酌情考虑保留标识参数。

```java
// 拆分成两个函数的调用方式
boolean isVip = false;
//...省略其他逻辑...
if (isVip) {
  buyCourseForVip(userId, courseId);
} else {
  buyCourse(userId, courseId);
}

// 保留标识参数的调用方式更加简洁
boolean isVip = false;
//...省略其他逻辑...
buyCourse(userId, courseId, isVip);
```

“根据参数是否为 null”来控制逻辑的情况。针对这种情况，我们也应该将其拆分成多个函数。拆分之后的函数职责更明确，不容易用错。

```java
import org.coder.design.patterns._3_programming_specification._2_testability.Transaction;

public List<Transaction> selectTransactions(Long userId, Date startDate, Date endDate) {
    if (startDate != null && endDate != null) {
        // 查询两个时间区间的transactions
    }
    if (startDate != null && endDate == null) {
        // 查询startDate之后的所有transactions
    }
    if (startDate == null && endDate != null) {
        // 查询endDate之前的所有transactions
    }
    if (startDate == null && endDate == null) {
        // 查询所有的transactions
    }
}

// 拆分成多个public函数，更加清晰、易用
public List<Transaction> selectTransactionsBetween(Long userId, Date startDate, Date endDate) {
    return selectTransactions(userId, startDate, endDate);
}

public List<Transaction> selectTransactionsStartWith(Long userId, Date startDate) {
    return selectTransactions(userId, startDate, null);
}

public List<Transaction> selectTransactionsEndWith(Long userId, Date endDate) {
    return selectTransactions(userId, null, endDate);
}

public List<Transaction> selectAllTransactions(Long userId) {
    return selectTransactions(userId, null, null);
}

private List<Transaction> selectTransactions(Long userId, Date startDate, Date endDate) {
    // ...
}
```

## 函数设计要职责单一

相对于类和模块，函数的粒度比较小，代码行数少，所以在应用单一职责原则的时候，没有像应用到类或者模块那样模棱两可，能多单一就多单一。

```java
import org.apache.commons.lang3.StringUtils;

public boolean checkUserIfExisting(String telephone, String username, String email) {
    if (!StringUtils.isBlank(telephone)) {
        User user = userRepo.selectUserByTelephone(telephone);
        return user != null;
    }

    if (!StringUtils.isBlank(username)) {
        User user = userRepo.selectUserByUsername(username);
        return user != null;
    }

    if (!StringUtils.isBlank(email)) {
        User user = userRepo.selectUserByEmail(email);
        return user != null;
    }

    return false;
}

// 拆分成三个函数
public boolean checkUserIfExistingByTelephone(String telephone);

public boolean checkUserIfExistingByUsername(String username);

public boolean checkUserIfExistingByEmail(String email);
```

## 移除过深的嵌套层次

去掉多余的 if 或 else 语句。

```java
import java.util.ArrayList;// 示例一
public double caculateTotalAmount(List<Order> orders) {
    if (orders == null || orders.isEmpty()) {
        return 0.0;
    } else { // 此处的else可以去掉
        double amount = 0.0;
        for (Order order : orders) {
            if (order != null) {
                amount += (order.getCount() * order.getPrice());
            }
        }
        return amount;
    }
}

// 示例二
public List<String> matchStrings(List<String> strList, String substr) {
    List<String> matchedStrings = new ArrayList<>();
    if (strList != null && substr != null) {
        for (String str : strList) {
            if (str != null) { // 跟下面的if语句可以合并在一起
                if (str.contains(substr)) {
                    matchedStrings.add(str);
                }
            }
        }
    }
    return matchedStrings;
}
```

使用编程语言提供的 continue、break、return 关键字，提前退出嵌套。

```java
import java.util.ArrayList;// 重构前的代码
public List<String> matchStrings(List<String> strList, String substr) {
    List<String> matchedStrings = new ArrayList<>();
    if (strList != null && substr != null) {
        for (String str : strList) {
            if (str != null && str.contains(substr)) {
                matchedStrings.add(str);
                // 此处还有10行代码...
            }
        }
    }
    return matchedStrings;
}

// 重构后的代码：使用continue提前退出
public List<String> matchStrings(List<String> strList, String substr) {
    List<String> matchedStrings = new ArrayList<>();
    if (strList != null && substr != null) {
        for (String str : strList) {
            if (str == null || !str.contains(substr)) {
                continue;
            }
            matchedStrings.add(str);
            // 此处还有10行代码...
        }
    }
    return matchedStrings;
}
```

调整执行顺序来减少嵌套。

```java
import java.util.ArrayList;
import java.util.Collections;// 重构前的代码
public List<String> matchStrings(List<String> strList, String substr) {
    List<String> matchedStrings = new ArrayList<>();
    if (strList != null && substr != null) {
        for (String str : strList) {
            if (str != null) {
                if (str.contains(substr)) {
                    matchedStrings.add(str);
                }
            }
        }
    }
    return matchedStrings;
}

// 重构后的代码：先执行判空逻辑，再执行正常逻辑
public List<String> matchStrings(List<String> strList, String substr) {
    if (strList == null || substr == null) { //先判空
        return Collections.emptyList();
    }

    List<String> matchedStrings = new ArrayList<>();
    for (String str : strList) {
        if (str != null) {
            if (str.contains(substr)) {
                matchedStrings.add(str);
            }
        }
    }
    return matchedStrings;
}
```

将部分嵌套逻辑封装成函数调用，以此来减少嵌套。

```java
// 重构前的代码
public List<String> appendSalts(List<String> passwords) {
  if (passwords == null || passwords.isEmpty()) {
    return Collections.emptyList();
  }
  
  List<String> passwordsWithSalt = new ArrayList<>();
  for (String password : passwords) {
    if (password == null) {
      continue;
    }
    if (password.length() < 8) {
      // ...
    } else {
      // ...
    }
  }
  return passwordsWithSalt;
}

// 重构后的代码：将部分逻辑抽成函数
public List<String> appendSalts(List<String> passwords) {
  if (passwords == null || passwords.isEmpty()) {
    return Collections.emptyList();
  }

  List<String> passwordsWithSalt = new ArrayList<>();
  for (String password : passwords) {
    if (password == null) {
      continue;
    }
    passwordsWithSalt.add(appendSalt(password));
  }
  return passwordsWithSalt;
}

private String appendSalt(String password) {
  String passwordWithSalt = password;
  if (password.length() < 8) {
    // ...
  } else {
    // ...
  }
  return passwordWithSalt;
}
```

通过使用多态来替代 if-else、switch-case 条件判断的方法。

## 学会使用解释性变量

常量取代魔法数字。

```java
public double CalculateCircularArea(double radius) {
    return (3.1415) * radius * radius;
}

// 常量替代魔法数字
public static final Double PI = 3.1415;

public double CalculateCircularArea(double radius) {
    return PI * radius * radius;
}
```

使用解释性变量来解释复杂表达式。

```java
public static void main(String[] args) {
    if (date.after(SUMMER_START) && date.before(SUMMER_END)) {
        // ...
    } else {
        // ...
    }

// 引入解释性变量后逻辑更加清晰
    boolean isSummer = date.after(SUMMER_START) && date.before(SUMMER_END);
    if (isSummer) {
        // ...
    } else {
        // ...
    }
}
```