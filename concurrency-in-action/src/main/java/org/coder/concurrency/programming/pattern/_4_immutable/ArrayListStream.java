package org.coder.concurrency.programming.pattern._4_immutable;

import java.util.Arrays;
import java.util.List;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ArrayListStream {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("Java", "Thread", "Concurrency", "Scale", "Clojure");

        // list 虽然是在并行的环境下运行的，但是在 stream 的每一个操作中都是一个全新的 List，根本不会影响到原始的 list，这样也是符合不可变
        // 对象的基本思想
        list.parallelStream().map(String::toUpperCase).forEach(System.out::println);
        list.forEach(System.out::println);
    }
}
