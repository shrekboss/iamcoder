package org.coder.design.patterns._4_design_patterns._3_behavior._5_state.look_up_table;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public enum Event {
    GOT_MUSHROOM(0),
    GOT_CAPE(1),
    GOT_FIRE(2),
    MET_MONSTER(3);

    private final int value;

    private Event(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

