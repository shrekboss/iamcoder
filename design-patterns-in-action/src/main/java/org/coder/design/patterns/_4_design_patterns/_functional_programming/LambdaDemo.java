package org.coder.design.patterns._4_design_patterns._functional_programming;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
public class LambdaDemo {

    public static void main(String[] args) {
        Optional<Integer> result = Stream.of("f", "ba", "hello")
                .map(s -> s.length())
                .filter(l -> l <= 3)
                .max((o1, o2) -> o1 - o2);

        // 还原为函数接口的实现方式
        Optional<Integer> result2 = Stream.of("fo", "bar", "hello").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer l) {
                return l <= 3;
            }
        }).max(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
    }
}
