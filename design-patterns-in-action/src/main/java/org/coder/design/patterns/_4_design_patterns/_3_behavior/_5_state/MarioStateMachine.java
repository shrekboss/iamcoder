package org.coder.design.patterns._4_design_patterns._3_behavior._5_state;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MarioStateMachine {
    private int score;
    private State currentState;

    public MarioStateMachine() {
        this.score = 0;
        this.currentState = State.SMALL;
    }

    public void obtainMushRoom() {
        this.score += 100;
        this.currentState = State.SUPER;
    }

    public void obtainCape() {
        //TODO
    }

    public void obtainFireFlower() {
        //TODO
    }

    public void meetMonster() {
        //TODO
    }

    public int getScore() {
        return this.score;
    }

    public State getCurrentState() {
        return this.currentState;
    }
}
