## 函数式编程

### 何为无状态？

参考代码：

- [FunctionalProgramming.java](FunctionalProgramming.java)

### Stream 类

参考代码：

- [StreamDemo.java](StreamDemo.java)

### Lambda 表达式

```java
import java.util.stream.BaseStream;

// Stream中map函数的定义：
public interface Stream<T> extends BaseStream<T, Stream<T>> {
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);
    //...省略其他函数...
}

// Stream中map的使用方法：
Stream.of("fo","bar","hello").map(new Function<String, Integer>() {
    @Override
    public Integer apply (String s){
        return s.length();
    }
});

// 用Lambda表达式简化后的写法：
Stream.of("fo","bar","hello").map(s ->s.length());
```

参考代码：

- [LambdaDemo.java](LambdaDemo.java)

### Functional Interface

Java 提供的 Function、Predicate 这两个函数接口的源码

```java
import java.util.Objects;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);  // 只有这一个未实现的方法

    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }
}

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t); // 只有这一个未实现的方法

    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default Predicate<T> negate() {
        return (t) -> !test(t);
    }

    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    static <T> Predicate<T> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : object -> targetRef.equals(object);
    }
}
```
### Guava 对函数式编程的增强

Google Guava 并没有提供太多函数式编程的支持，仅仅封装了几个遍历集合操作的接口。

```text
Iterables.transform(Iterable, Function);
Iterators.transform(Iterator, Function);
Collections.transfrom(Collection, Function);
Lists.transform(List, Function);
Maps.transformValues(Map, Function);
Multimaps.transformValues(Mltimap, Function);
...
Iterables.filter(Iterable, Predicate);
Iterators.filter(Iterator, Predicate);
Collections2.filter(Collection, Predicate);
...
```

