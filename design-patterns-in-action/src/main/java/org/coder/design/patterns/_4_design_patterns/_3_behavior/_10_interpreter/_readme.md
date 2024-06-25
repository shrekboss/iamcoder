## 解释器模式

### 定义了一个新的加减乘除计算“语言”

```java
import java.util.Deque;

public class ExpressionInterpreter {
    private Deque<Long> numbers = new LinkedList<>();

    public long interpret(String expression) {
        String[] elements = expression.split(" ");
        int length = elements.length;
        for (int i = 0; i < (length + 1) / 2; ++i) {
            numbers.addLast(Long.parseLong(elements[i]));
        }

        for (int i = (length + 1) / 2; i < length; ++i) {
            String operator = elements[i];
            boolean isValid = "+".equals(operator) || "-".equals(operator)
                    || "*".equals(operator) || "/".equals(operator);
            if (!isValid) {
                throw new RuntimeException("Expression is invalid: " + expression);
            }

            long number1 = numbers.pollFirst();
            long number2 = numbers.pollFirst();
            long result = 0;
            if (operator.equals("+")) {
                result = number1 + number2;
            } else if (operator.equals("-")) {
                result = number1 - number2;
            } else if (operator.equals("*")) {
                result = number1 * number2;
            } else if (operator.equals("/")) {
                result = number1 / number2;
            }
            numbers.addFirst(result);
        }

        if (numbers.size() != 1) {
            throw new RuntimeException("Expression is invalid: " + expression);
        }

        return numbers.pop();
    }
}
```

> 定义的语法规则有两类表达式，一类是数字，一类是运算符，运算符又包括加减乘除。利用解释器模式，把解析的工作拆分到
> NumberExpression、AdditionExpression、SubtractionExpression、MultiplicationExpression、DivisionExpression 这样五个解析类中。

参考代码如下：

- [Expression.java](Expression.java)
    - [AdditionExpression.java](AdditionExpression.java)
    - [DivisionExpression.java](DivisionExpression.java)
    - [MultiplicationExpression.java](MultiplicationExpression.java)
    - [SubtractionExpression.java](SubtractionExpression.java)
    - [NumberExpression.java](NumberExpression.java)
- [ExpressionInterpreter.java](ExpressionInterpreter.java)

### 监控系统自定义告警规则

> 每分钟 API 总出错数超过 100 或者每分钟 API 总调用数超过 10000 就触发告警。
> api_error_per_minute > 100 || api_count_per_minute > 10000
