package org.coder.concurrency.programming.pattern._4_immutable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ImmutableDemo {
    public static void main(String[] args) {
        List<String> originalList = new ArrayList<>();
        originalList.add("a");
        originalList.add("b");
        originalList.add("c");

        List<String> jdkUnmodifiableList = Collections.unmodifiableList(originalList);
        List<String> guavaImmutableList = ImmutableList.copyOf(originalList);

        // 抛出UnsupportedOperationException
        // jdkUnmodifiableList.add("d");
        // 抛出UnsupportedOperationException
        // guavaImmutableList.add("d");
        originalList.add("d");

        print(originalList); // a b c d
        print(jdkUnmodifiableList); // a b c d
        print(guavaImmutableList); // a b c
    }

    private static void print(List<String> list) {
        for (String s : list) {
            System.out.print(s + " ");
        }
        System.out.println();
    }
}
