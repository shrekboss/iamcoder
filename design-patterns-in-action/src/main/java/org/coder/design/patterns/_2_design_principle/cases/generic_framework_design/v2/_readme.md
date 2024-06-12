## 性能计数器框架

> 对版本 1 的设计与实现进行重构，解决版本 1 存在的设计问题，让它满足之前学过的设计原则、思想、编程规范。重点来看一下
> Aggregator 和 ConsoleReporter、EmailReporter 这几个类。

### Aggregator 类存在的问题

> 一旦越来越多的统计功能添加进来之后，这个函数的代码量会持续增加，可读性、可维护性就变差了。

根据原始数据，计算得到统计数据。我们可以将这部分逻辑移动到 Aggregator 类中。这样 Aggregator 类就不仅仅是只包含统计方法的工具类了。

按照这个思路，重构之后参考代码：[Aggregator.java](Aggregator.java)

### ConsoleReporter 和 EmailReporter

> - 在这两个类中，从数据库中取数据、做统计的逻辑都是相同的，可以抽取出来复用，否则就违反了 DRY 原则。
> - 整个类负责的事情比较多，不相干的逻辑糅合在里面，职责不够单一。特别是显示部分的代码可能会比较复杂（比如 Email
    的显示方式），最好能将这部分显示逻辑剥离出来，设计成一个独立的类。
> - 除此之外，因为代码中涉及线程操作，并且调用了 Aggregator 的静态函数，所以代码的可测试性也有待提高。

按照这个思路，重构之后参考代码：

- [StatViewer.java](StatViewer.java)
    - [ConsoleViewer.java](ConsoleViewer.java)
    - [EmailViewer.java](EmailViewer.java)
- [ConsoleReporter.java](ConsoleReporter.java)
- [EmailReporter.java](EmailReporter.java)
- [PerfCounterTest.java](PerfCounterTest.java)

