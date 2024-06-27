package org.coder.design.patterns._5_open_source_practices._functional_programming;

import java.util.Optional;
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
public class StreamDemo {

    public static void main(String[] args) {
        Optional<Integer> result = Stream.of("f", "ba", "hello") // of返回Stream<String>对象
                .map(s -> s.length()) // map返回Stream<Integer>对象
                .filter(l -> l <= 3) // filter返回Stream<Integer>对象
                .max((o1, o2) -> o1 - o2); // max终止操作：返回Optional<Integer>
        System.out.println(result.get()); // 输出2

    }
}
