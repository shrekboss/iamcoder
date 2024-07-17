package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 6.2 Collector在Stream中的使用
 * 在6.1.3节中，我们已经体验过了collect操作：将Stream中的元素聚合到一个List容器中；
 * 对Stream中的元素进行分组并且聚合到一个Map容器中，这一切主要得益于Collector接口在Stream collect操作中发挥的作用。
 * 本节就来深入解析Collector接口的使用，在本节的最后一部分，我们还会利用所学知识自定义一个Collector，以加深对Collector接口的认识。
 * <p>
 * Collector在Stream中主要用途大致包含如下三项。
 * 1.Reduce和Summarizing Stream中的元素到一个单一的新的输出。
 * 2.对Stream中的元素进行分组（Grouping）。
 * 3.对Stream中的元素进行分区（Partitioning）。
 * <p>
 * 6.2.1 初识Collector
 * 在深入掌握Collector之前，我们需要先学习如何使用Collector，以对其有一个基本粗浅的理解。
 * 下面通过几个简单的代码示例来感受一下Collector的用法。
 * <p>
 * 首先定义一个简单的商品类，其中只包含两个字段：name(String类型)和price(double类型)。
 * <p>
 * Stream中包含了若干Production元素，借助于Stream Collector接口，我们可以实现很多非常有意思且功能强大的功能。
 */
public class CollectorExample1 {
    /**
     * 1.Reduce和Summarizing Stream操作
     * 计算Stream中所有衣服商品的价格总和，可以借助于summingDouble()方法来实现。
     * <p>
     * 虽然通过前文所学的Stream操作可以很容易地实现类似于collect summingDouble的功能，但是相较于传统的操作方式，collect操作显然要强大优雅很多。
     */
    @Test
    public void test() {
        //构造Stream，元素类型为Production
        Stream<Production> stream = Stream.of(
                new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d)
        );
        //过滤，只保留衣服元素并且返回一个新的Stream
        Double totalPrice = stream.filter(p -> p.getName().equals("cloth"))
                //执行collect操作，通过summingDouble()方法计算所有商品的总价
                .collect(Collectors.summingDouble(Production::getPrice));
        //断言
        assert totalPrice == 99.99d + 199.99d;
    }

    /**
     * 当然，上述代码片段即使不使用collect操作也是非常容易实现的，下面分别使用Double Stream的sum操作和reduce操作来实现，代码片段如下所示。
     */
    @Test
    public void test2() {
        //构造Stream，元素类型为Production
        Stream<Production> stream = Stream.of(
                new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d)
        );

        Double totalPrice = stream.filter(p -> p.getName().equals("cloth"))
                //将Stream<Production>转换为DoubleStream
                .mapToDouble(Production::getPrice)
                //执行sum操作
                .sum();
        //断言
        assert totalPrice == 99.99d + 199.99d;
    }

    /**
     * 同上
     */
    @Test
    public void test3() {
        //构造Stream，元素类型为Production
        Stream<Production> stream = Stream.of(
                new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d)
        );

        Double totalPrice = stream.filter(p -> p.getName().equals("cloth"))
                .mapToDouble(Production::getPrice)
                //执行reduce操作
                .reduce(0, Double::sum);
        //断言成功
        assert totalPrice == 99.99d + 199.99d;
    }

    /**
     * 2.简单了解分组操作
     * 现在我们根据品类对商品进行分类，并且计算每一个品类商品的总价，应该如何操作呢？
     * 首先我们使用传统的方式实现该功能，代码如下。
     */
    @Test
    public void test4() {
        List<Production> list = Arrays.asList(new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d));

        final Map<String, Double> prodPrice = new HashMap<>();

        for (Production p : list) {
            String prodName = p.getName();
            double price = p.getPrice();
            //如果prodPrice包含品类名称，则进行累加
            if (prodPrice.containsKey(prodName)) {
                Double totalPrice = prodPrice.get(prodName);
                prodPrice.put(prodName, totalPrice + price);
            } else {//否则直接存入prodPrice
                prodPrice.put(prodName, price);
            }
        }
        //断言语句
        assert prodPrice.size() == 4;
        assert prodPrice.get("T-Shirt") == 43.34d;
        assert prodPrice.get("cloth") == 99.99d + 199.99d;
        assert prodPrice.get("shoe") == 123.8d + 32.5d;
        assert prodPrice.get("hat") == 26.5d;

    }

    /**
     * 通过传统的方式来做到类似的操作也没有任何问题，但是代码中存在逻辑判断，以及显示分组的操作(需要定义Map)，那么使用Collector重构之后呢？
     * 代码很明显简洁了很多，并且隐藏了诸多细节，比如，不用显式定义Map，不用进行逻辑判断，重构之后的代码如下。
     * <p>
     * 运行重构之后的代码片段可以实现与传统方式同样的功能，但是看起来代码精简了很多，当然代码精简只是运用Stream的一个附加值而已。
     * 在进行collect操作之前所有的操作都是以lazy的方式进行，除此之外，针对Stream的每一个操作都可以轻而易举地做到并行运算才是Stream为我们带来的核心价值。
     */
    @Test
    public void test5() {
        Stream<Production> stream = Stream.of(
                new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d)
        );
        Map<String, Double> groupingPrice = stream.collect(
                //调用groupingBy函数
                Collectors.groupingBy(
                        //Function
                        Production::getName,
                        //针对down Stream的Collector操作
                        Collectors.summingDouble(Production::getPrice)
                )
        );

        assert groupingPrice.size() == 4;
        assert groupingPrice.get("T-Shirt") == 43.34d;
        assert groupingPrice.get("cloth") == 99.99d + 199.99d;
        assert groupingPrice.get("shoe") == 123.8d + 32.5d;
        assert groupingPrice.get("hat") == 26.5d;
    }

    /**
     * 3.简单了解分区操作
     * 在一个商品列表中，每一件商品的价格都不尽相同，我们可以根据商品的价格将其分为高低两个档位，
     * 也就是针对商品的价格对商品进行分区操作，比如，0~100元为低档，100元以上为高档，示例代码如下。
     */
    @Test
    public void test6() {
        List<Production> list = Arrays.asList(new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d));
        //定义用于存放结果的map
        final Map<String, List<Production>> prodLevel = new HashMap<>();
        //以循环的方式遍历每一个production元素
        for (Production p : list) {
            //计算
            String level = calculateLevel(p.getPrice());
            //调用Map的computeIfAbsent方法
            prodLevel.computeIfAbsent(level, key -> new ArrayList<>()).add(p);
        }

        System.out.println(prodLevel);
    }

    /**
     * 根据价位进行分区计算
     *
     * @param price
     * @return
     */
    private String calculateLevel(double price) {
        if (price > 0 && price < 100) {
            return "LOW";
        } else if (price >= 100) {
            return "HIGH";
        } else {
            throw new IllegalArgumentException("Illegal production price.");
        }
    }

    /**
     * 通过Collectors的分区操作我们很容易就可以实现类似的功能，代码片段如下所示，Key值为True时代表高端商品，为False时代表低端商品集合。
     */
    @Test
    public void test7() {
        Stream<Production> stream = Stream.of(
                new Production("T-Shirt", 43.34d),
                new Production("cloth", 99.99d),
                new Production("shoe", 123.8d),
                new Production("hat", 26.5d),
                new Production("cloth", 199.99d),
                new Production("shoe", 32.5d)
        );
        Map<Boolean, List<Production>> level = stream.collect(Collectors.partitioningBy(p -> p.getPrice() > 100));
        System.out.println(level);
    }
}