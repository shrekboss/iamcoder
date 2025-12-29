package org.coder.design.patterns._4_design_patterns._3_behavior._5_state;

import org.coder.design.patterns._4_design_patterns._3_behavior._5_state.state_model.MarioStateMachine;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ApplicationDemo {
    public static void main(String[] args) {
        MarioStateMachine mario = new MarioStateMachine();
        mario.obtainMushRoom();
        int score = mario.getScore();
        State state = mario.getCurrentState();
        System.out.println("Mario score: " + score + "; state: " + state);
    }
}
