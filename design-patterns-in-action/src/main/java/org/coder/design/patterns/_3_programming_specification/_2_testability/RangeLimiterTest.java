package org.coder.design.patterns._3_programming_specification._2_testability;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RangeLimiterTest {
    @Test
    public void testMove_betweenRange() {
        RangeLimiter rangeLimiter = new RangeLimiter();
        assertTrue(rangeLimiter.move(1));
        assertTrue(rangeLimiter.move(3));
        assertTrue(rangeLimiter.move(-5));
    }

    @Test
    public void testMove_exceedRange() {
        RangeLimiter rangeLimiter = new RangeLimiter();
        assertFalse(rangeLimiter.move(6));
    }
}

class RangeLimiter {
    private static AtomicInteger position = new AtomicInteger(0);
    public static final int MAX_LIMIT = 5;
    public static final int MIN_LIMIT = -5;

    public boolean move(int delta) {
        int currentPos = position.addAndGet(delta);
        boolean betweenRange = (currentPos <= MAX_LIMIT) && (currentPos >= MIN_LIMIT);
        return betweenRange;
    }
}

