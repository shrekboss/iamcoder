package org.coder.concurrency.programming.pattern._11_two_phase_termination.reference;

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
@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(K k);
}
