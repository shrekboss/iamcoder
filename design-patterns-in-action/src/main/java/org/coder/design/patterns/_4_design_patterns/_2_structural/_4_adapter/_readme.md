## 适配器模式

配器模式有两种实现方式：类适配器和对象适配器。其中，类适配器使用继承关系来实现，对象适配器使用组合关系来实现。

具体的代码实现如下所示。

```java
// 类适配器: 基于继承
public interface ITarget {
    void f1();

    void f2();

    void fc();
}

public class Adaptee {
    public void fa() {
        //...
    }

    public void fb() {
        //...
    }

    public void fc() {
        //...
    }
}

public class Adaptor extends Adaptee implements ITarget {
    public void f1() {
        super.fa();
    }

    public void f2() {
        //...重新实现f2()...
    }

    // 这里fc()不需要实现，直接继承自Adaptee，这是跟对象适配器最大的不同点
}

// 对象适配器：基于组合
public interface ITarget {
    void f1();

    void f2();

    void fc();
}

public class Adaptee {
    public void fa() {
        //...
    }

    public void fb() {
        //...
    }

    public void fc() {
        //...
    }

    public class Adaptor implements ITarget {
        private Adaptee adaptee;

        public Adaptor(Adaptee adaptee) {
            this.adaptee = adaptee;
        }

        public void f1() {
            adaptee.fa(); //委托给Adaptee
        }

        public void f2() {
            //...重新实现f2()...
        }

        public void fc() {
            adaptee.fc();
        }
    }
}
```

### 封装有缺陷的接口设计

> 假设依赖的外部系统在接口设计方面有缺陷（比如包含大量静态方法），引入之后会影响到自身代码的可测试性。为了隔离设计上的缺陷，希望对外部系统提供的接口进行二次封装，抽象出更好的接口设计，这个时候就可以使用适配器模式了。

```java
public class CD { //这个类来自外部sdk，我们无权修改它的代码
    
    public static void staticFunction1() {
        //...
    }

    public void uglyNamingFunction2() {
        //...
    }

    public void tooManyParamsFunction3(int paramA, int paramB) {
        //...
    }

    public void lowPerformanceFunction4() {
        //...
    }
}

// 使用适配器模式进行重构
public interface ITarget {
    void function1();

    void function2();

    void function3(ParamsWrapperDefinition paramsWrapper);

    void function4();
    //...
}

// 注意：适配器类的命名不一定非得末尾带Adaptor
public class CDAdaptor extends CD implements ITarget {
    //...
    public void function1() {
        super.staticFunction1();
    }

    public void function2() {
        super.uglyNamingFucntion2();
    }

    public void function3(ParamsWrapperDefinition paramsWrapper) {
        super.tooManyParamsFunction3(paramsWrapper.getParamA(), paramsWrapper.getParamB());
    }

    public void function4() {
        //...reimplement it...
    }
}
```

### 统一多个类的接口设计

某个功能的实现依赖多个外部系统（或者说类）。通过适配器模式，将它们的接口适配为统一的接口定义，然后我们就可以使用多态的特性来复用代码逻辑。

具体代码如下

```java
import java.util.ArrayList;

public class ASensitiveWordsFilter { // A敏感词过滤系统提供的接口
    //text是原始文本，函数输出用***替换敏感词之后的文本
    public String filterSexyWords(String text) {
        // ...
    }

    public String filterPoliticalWords(String text) {
        // ...
    }
}

public class BSensitiveWordsFilter { // B敏感词过滤系统提供的接口
    public String filter(String text) {
        //...
    }
}

public class CSensitiveWordsFilter { // C敏感词过滤系统提供的接口
    public String filter(String text, String mask) {
        //...
    }
}

// 未使用适配器模式之前的代码：代码的可测试性、扩展性不好
public class RiskManagement {
    private ASensitiveWordsFilter aFilter = new ASensitiveWordsFilter();
    private BSensitiveWordsFilter bFilter = new BSensitiveWordsFilter();
    private CSensitiveWordsFilter cFilter = new CSensitiveWordsFilter();

    public String filterSensitiveWords(String text) {
        String maskedText = aFilter.filterSexyWords(text);
        maskedText = aFilter.filterPoliticalWords(maskedText);
        maskedText = bFilter.filter(maskedText);
        maskedText = cFilter.filter(maskedText, "***");
        return maskedText;
    }
}

// 使用适配器模式进行改造
public interface ISensitiveWordsFilter { // 统一接口定义
    String filter(String text);
}

public class ASensitiveWordsFilterAdaptor implements ISensitiveWordsFilter {

    private ASensitiveWordsFilter aFilter;

    public String filter(String text) {
        String maskedText = aFilter.filterSexyWords(text);
        return aFilter.filterPoliticalWords(maskedText);
    }
}
//...省略BSensitiveWordsFilterAdaptor、CSensitiveWordsFilterAdaptor...

// 扩展性更好，更加符合开闭原则，如果添加一个新的敏感词过滤系统，
// 这个类完全不需要改动；而且基于接口而非实现编程，代码的可测试性更好。
public class RiskManagement {
    private List<ISensitiveWordsFilter> filters = new ArrayList<>();

    public void addSensitiveWordsFilter(ISensitiveWordsFilter filter) {
        filters.add(filter);
    }

    public String filterSensitiveWords(String text) {
        String maskedText = text;
        for (ISensitiveWordsFilter filter : filters) {
            maskedText = filter.filter(maskedText);
        }
        return maskedText;
    }
}
```

### 替换依赖的外部接口

把项目中依赖的一个外部系统替换为另一个外部系统的时候，利用适配器模式，可以减少对代码的改动。

具体的代码示例如下所示

```java
// 外部系统A
public interface IA {
    //...
    void fa();
}

public class A implements IA {
    //...
    public void fa() {
        //... 
    }
}

// 在我们的项目中，外部系统A的使用示例
public class Demo {
    private IA a;

    public Demo(IA a) {
        this.a = a;
    }
    //...

    public static void main(String[] args) {
        Demo d = new Demo(new A());
    }
}


// 将外部系统A替换成外部系统B
public class BAdaptor implements IA {
    private B b;

    public BAdaptor(B b) {
        this.b = b;
    }

    public void fa() {
        //...
        b.fb();
    }

    public static void main(String[] args) {
        // 借助BAdaptor，Demo的代码中，调用IA接口的地方都无需改动，
        // 只需要将BAdaptor如下注入到Demo即可。
        Demo d = new Demo(new BAdaptor(new B()));
    }
}


```

### 兼容老版本接口

在做版本升级的时候，对于一些要废弃的接口，我们不直接将其删除，而是暂时保留，并且标注为 deprecated，并将内部实现逻辑委托为新的接口实现。这样做的好处是，让使用它的项目有个过渡期，而不是强制进行代码修改。这也可以粗略地看作适配器模式的一个。

代码如下

```java
import java.util.Collection;
import java.util.Enumeration;

public class Collections {
  public static Emueration enumeration(final Collection c) {
    return new Enumeration() {
      Iterator i = c.iterator();
      
      public boolean hasMoreElements() {
        return i.hashNext();
      }
      
      public Object nextElement() {
        return i.next();
      }
    };
  }
}
```

### 适配不同格式的数据

适配器模式主要用于接口的适配，实际上，它还可以用在不同格式的数据之间的适配。比如，把从不同征信系统拉取的不同格式的征信数据，统一为相同的格式，以方便存储和使用。再比如，Java 中的 Arrays.asList() 也可以看作一种数据适配器，将数组类型的数据转化为集合容器类型。

```java
List<String> stooges = Arrays.asList("Larry", "Moe", "Curly");
```


### 剖析适配器模式在 Java 日志中的应用

Slf4j 的出现晚于 JUL(java.util.logging)、JCL(Jakarta Commons Logging)、log4j 等日志框架，所以，这些日志框架也不可能牺牲掉版本兼容性，将接口改造成符合 Slf4j 接口规范。Slf4j 也事先考虑到了这个问题，所以，它不仅仅提供了统一的接口定义，还提供了针对不同日志框架的适配器。对不同日志框架的接口进行二次封装，适配成统一的 Slf4j 接口定义。

具体的代码示例如下所示

```java 
package org.slf4j;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * slf4j统一的接口定义
 */
public interface Logger {
  boolean isTraceEnabled();
  void trace(String msg);
  void trace(String format, Object arg);
  void trace(String format, Object arg1, Object arg2);
  void trace(String format, Object[] argArray);
  void trace(String msg, Throwable t);
 
  boolean isDebugEnabled();
  void debug(String msg);
  void debug(String format, Object arg);
  void debug(String format, Object arg1, Object arg2);
  void debug(String format, Object[] argArray);
  void debug(String msg, Throwable t);

  //...省略info、warn、error等一堆接口
}

/**
 * log4j日志框架的适配器
 * Log4jLoggerAdapter实现了LocationAwareLogger接口，
 * 其中LocationAwareLogger继承自Logger接口，
 * 也就相当于Log4jLoggerAdapter实现了Logger接口。
 */
public final class Log4jLoggerAdapter extends MarkerIgnoringBase
  implements LocationAwareLogger, Serializable {
  final transient org.apache.log4j.Logger logger; // log4j
 
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }
 
  public void debug(String msg) {
    logger.log(FQCN, Level.DEBUG, msg, null);
  }
 
  public void debug(String format, Object arg) {
    if (logger.isDebugEnabled()) {
      FormattingTuple ft = MessageFormatter.format(format, arg);
      logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }
  }
 
  public void debug(String format, Object arg1, Object arg2) {
    if (logger.isDebugEnabled()) {
      FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
      logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }
  }
 
  public void debug(String format, Object[] argArray) {
    if (logger.isDebugEnabled()) {
      FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
      logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }
  }
 
  public void debug(String msg, Throwable t) {
    logger.log(FQCN, Level.DEBUG, msg, t);
  }
  //...省略一堆接口的实现...
}
```

如果一些老的项目没有使用 Slf4j，而是直接使用比如 JCL 来打印日志，那如果想要替换成其他日志框架，比如 log4j，该怎么办呢？实际上，Slf4j 不仅仅提供了从其他日志框架到 Slf4j 的适配器，还提供了反向适配器，也就是从 Slf4j 到其他日志框架的适配。我们可以先将 JCL 切换为 Slf4j，然后再将 Slf4j 切换为 log4j。经过两次适配器的转换，我们就能成功将 JCL 切换为了 log4j。
