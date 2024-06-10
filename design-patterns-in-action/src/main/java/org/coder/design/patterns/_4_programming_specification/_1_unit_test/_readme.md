## 单元测试

> 测试 Text 类中的 toNumber() 函数的正确性

参考代码：[Text.java](Text.java)

- 为了保证测试的全面性，针对 toNumber() 函数，需要设计下面这样几个测试用例:
    - 如果字符串只包含数字：“123”，toNumber() 函数输出对应的整数：123。
    - 如果字符串是空或者 null，toNumber() 函数返回：null。
    - 如果字符串包含首尾空格：“ 123”，“123 ”，“ 123 ”，toNumber() 返回对应的整数：123。
    - 如果字符串包含多个首尾空格：“ 123 ”，toNumber() 返回对应的整数：123；
    - 如果字符串包含非数字字符：“123a4”，“123 4”，toNumber() 返回 null；

参考代码：

- [Assert.java](Assert.java)
- [TextTestCases.java](TextTestCases.java)
- [TestCaseRunner.java](TestCaseRunner.java)
- [TextTestCasesWithJunit.java](TextTestCasesWithJunit.java)