## 数值计算：注意精度、舍入和溢出问题

### 1. “危险”的Double

> 对于计算机而言，0.1 无法精确表达，这是浮点数计算造成精度损失的根源。

参考代码：[dangerousdouble](dangerousdouble)

[IEEE 754 标准]: https://en.wikipedia.org/wiki/IEEE_754

[查看数值转化为二进制的结果]: http://www.binaryconvert.com/

1. 浮点数避坑第一原则：要精确表示浮点数应该使用 BigDecimal。并且，使用 BigDecimal 的 Double 入参的构造方法同样存在精度丢失问题，应该使用
   String 入参的构造方法或者 BigDecimal.valueOf 方法来初始化。
2. 对浮点数做精确计算，参与计算的各种数值应该始终使用 BigDecimal，所有的计算都要通过 BigDecimal 的方法进行，切勿只是让
   BigDecimal 来走过场。任何一个环节出现精度损失，最后的计算结果可能都会出现误差。

scale 表示小数点右边的位数，而 precision 表示精度，也就是有效数字的长度

Formatter 类的相关源码 可以发现使用的舍入模式是 HALF_UP

```JAVA
else if(c ==Conversion.DECIMAL_FLOAT){
// Create a new BigDecimal with the desired precision.
int prec = (precision == -1 ? 6 : precision);
int scale = value.scale();

    if(scale >prec){
// more "scale" digits than the requested "precision"
int compPrec = value.precision();
        if(compPrec <=scale){
// case of 0.xxxxxx
value =value.

setScale(prec, RoundingMode.HALF_UP);
        }else{
compPrec -=(scale -prec);
value =new

BigDecimal(value.unscaledValue(),

scale,
        new

MathContext(compPrec));
        }
        }
        }
```

### 2. 考虑浮点数舍入和格式化的方式

参考代码：[rounding](rounding)

- 对于浮点数的格式化，如果使用 String.format 的话，需要认识到它使用的是四舍五入，可以考虑使用 DecimalFormat
  来明确指定舍入方式。但考虑到精度问题，我更建议使用 BigDecimal 来表示浮点数，并使用其 setScale 方法指定舍入的位数和方式。

  ```java
  BigDecimal num1 = new BigDecimal("3.35");
  BigDecimal num2 = num1.setScale(1,BigDecimal.ROUND_DOWN);
  System.out.println(num2);//3.3
  BigDecimal num3 = num1.setScale(1, BigDecimal.ROUND_HALF_UP);
  System.out.println(num3);//3.4
  ```

#### BigDecimal提供了 8 种舍入模式

- ROUND_UP，舍入远离零的舍入模式，在丢弃非零部分之前始终增加数字（始终对非零舍弃部分前面的数字加 1）。 需要注意的是，此舍入模式始终不会减少原始值。
- ROUND_DOWN，接近零的舍入模式，在丢弃某部分之前始终不增加数字（从不对舍弃部分前面的数字加 1，即截断）。
  需要注意的是，此舍入模式始终不会增加原始值。
- ROUND_CEILING，接近正无穷大的舍入模式。 如果 BigDecimal 为正，则舍入行为与 ROUND_UP 相同； 如果为负，则舍入行为与
  ROUND_DOWN 相同。 需要注意的是，此舍入模式始终不会减少原始值。
- ROUND_FLOOR，接近负无穷大的舍入模式。 如果 BigDecimal 为正，则舍入行为与 ROUND_DOWN 相同； 如果为负，则舍入行为与 ROUND_UP
  相同。 需要注意的是，此舍入模式始终不会增加原始值。
- ROUND_HALF_UP，向“最接近的”数字舍入。如果舍弃部分 >= 0.5，则舍入行为与 ROUND_UP 相同；否则，舍入行为与 ROUND_DOWN 相同。
  需要注意的是，这是我们大多数人在小学时就学过的舍入模式（四舍五入）。
- ROUND_HALF_DOWN，向“最接近的”数字舍入。如果舍弃部分 > 0.5，则舍入行为与 ROUND_UP 相同；否则，舍入行为与 ROUND_DOWN
  相同（五舍六入）。
- ROUND_HALF_EVEN，向“最接近的”数字舍入。这种算法叫做银行家算法，具体规则是，四舍六入，五则看前一位，如果是偶数舍入，如果是奇数进位，比如
  5.5 -> 6，2.5 -> 2。
- ROUND_UNNECESSARY，假设请求的操作具有精确的结果，也就是不需要进行舍入。如果计算结果产生不精确的结果，则抛出
  ArithmeticException。

#### 数据库（比如 MySQL）中的浮点数和整型数字

##### 定义

MySQL 中的整数根据能表示的范围有 TINYINT、SMALLINT、MEDIUMINT、INTEGER、BIGINT 等类型，浮点数包括单精度浮点数 FLOAT 和双精度浮点数
DOUBLE 和 Java 中的 float/double 一样，同样有精度问题。

##### 要解决精度问题，主要有两个办法

- 使用 DECIMAL 类型（和那些 INT 类型一样，都属于严格数值数据类型），比如 DECIMAL(13, 2) 或 DECIMAL(13, 4)。
- 使用整数保存到分，这种方式容易出错，万一读的时候忘记 /100 或者是存的时候忘记 *
  100，可能会引起重大问题。当然了，我们也可以考虑将整数和小数分开保存到两个整数字段。

### 3. 用equals做判等，就一定是对的吗？

参考代码：[equals](equals)

- equals 比较的是 BigDecimal 的 value 和 scale
- 只比较 BigDecimal 的 value，可以使用 compareTo 方法

### 4. 小心数值溢出问题

参考代码：[overflowissue](overflowissue)

- BigDecimal 是处理浮点数的专家，而 BigInteger 则是对大数进行科学计算的专家
- Math 类的 addExact、subtractExact 等 xxExact 方法进行数值运算，这些方法可以在数值溢出时主动抛出 ArithmeticException 异常