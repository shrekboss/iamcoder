package org.coder.concurrency.programming.pattern._4_immutable;

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
public class Immutable {

    private final List<String> list;

    public Immutable(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        //Collections.unmodifiableList(this.list)
        return this.list;
    }
}
