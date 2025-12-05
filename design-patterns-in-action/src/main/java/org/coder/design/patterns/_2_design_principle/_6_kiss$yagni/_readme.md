> - Keep It Simple and Stupid.
> - Keep It Short and Simple.
> - Keep It Simple and Straightforward.
> 不过，仔细看你就会发现，它们要表达的意思其实差不多，翻译成中文就是：尽量保持简单。
> 
> YAGNI 原则的英文全称是：You Ain’t Gonna Need It。直译就是：你不会需要它。这条原则也算是万金油了。当用在软件开发中的时候，它的意思是：不
> 要去设计当前用不到的功能；不要去编写当前用不到的代码。实际上，这条原则的核心思想就是：不要做过度设计。

## 代码行数越少就越“简单”吗？

> 检查输入的字符串 ipAddress 是否是合法的 IP 地址。

代码实现如下：

```java
// 第一种实现方式: 使用正则表达式
public boolean isValidIpAddressV1(String ipAddress) {
    if (StringUtils.isBlank(ipAddress)) return false;
    String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    return ipAddress.matches(regex);
}

// 第二种实现方式: 使用现成的工具类
public boolean isValidIpAddressV2(String ipAddress) {
    if (StringUtils.isBlank(ipAddress)) return false;
    String[] ipUnits = StringUtils.split(ipAddress, '.');
    if (ipUnits.length != 4) {
        return false;
    }
    for (int i = 0; i < 4; ++i) {
        int ipUnitIntValue;
        try {
            ipUnitIntValue = Integer.parseInt(ipUnits[i]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (ipUnitIntValue < 0 || ipUnitIntValue > 255) {
            return false;
        }
        if (i == 0 && ipUnitIntValue == 0) {
            return false;
        }
    }
    return true;
}

// 第三种实现方式: 不使用任何工具类
public boolean isValidIpAddressV3(String ipAddress) {
    char[] ipChars = ipAddress.toCharArray();
    int length = ipChars.length;
    int ipUnitIntValue = -1;
    boolean isFirstUnit = true;
    int unitsCount = 0;
    for (char c : ipChars) {
        if (c == '.') {
            if (ipUnitIntValue < 0 || ipUnitIntValue > 255) return false;
            if (isFirstUnit && ipUnitIntValue == 0) return false;
            if (isFirstUnit) isFirstUnit = false;
            ipUnitIntValue = -1;
            unitsCount++;
            continue;
        }
        if (c < '0' || c > '9') {
            return false;
        }
        if (ipUnitIntValue == -1) ipUnitIntValue = 0;
        ipUnitIntValue = ipUnitIntValue * 10 + (c - '0');
    }
    if (ipUnitIntValue < 0 || ipUnitIntValue > 255) return false;
    return unitsCount == 3;
}
```

- 第一种实现方式利用的是正则表达式，只用三行代码就把这个问题搞定了。它的代码行数最少，那是不是就最符合 KISS 原则呢？
    - 答案是否定的。虽然代码行数最少，看似最简单，实际上却很复杂。这正是因为它使用了正则表达式。
    - 一方面，正则表达式本身是比较复杂的，写出完全没有 bug 的正则表达本身就比较有挑战；
    - 另一方面，并不是每个程序员都精通正则表达式。

- 第二种实现方式使用了 StringUtils 类、Integer 类提供的一些现成的工具函数，来处理 IP 地址字符串。
- 第三种实现方式，不使用任何工具函数，而是通过逐一处理 IP 地址中的字符，来判断是否合法。

- 从代码行数上来说，这两种方式差不多。但是，第三种要比第二种更加有难度，更容易写出 bug。
    - 从可读性上来说，第二种实现方式的代码逻辑更清晰、更好理解。所以，在这两种实现方式中，第二种实现方式更加“简单”，更加符合
      KISS 原则。
    - 从性能的角度来说，选择第三种实现方式更好些。(**实际上是一种过度优化**)

## 代码逻辑复杂就违背 KISS 原则吗？

> [KMP 字符串匹配算法的代码实现](https://time.geekbang.org/column/article/71845)

参考代码如下：

- [KmpAlgorithm.java](KmpAlgorithm.java)

> 本身就复杂的问题，用复杂的方法解决，并不违背 KISS 原则。