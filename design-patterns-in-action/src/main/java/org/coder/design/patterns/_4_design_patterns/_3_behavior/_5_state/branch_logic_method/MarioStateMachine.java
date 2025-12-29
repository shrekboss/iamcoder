package org.coder.design.patterns._4_design_patterns._3_behavior._5_state.branch_logic_method;

import org.coder.design.patterns._4_design_patterns._3_behavior._5_state.State;

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
        if (currentState.equals(State.SMALL)) {
            this.currentState = State.SUPER;
            this.score += 100;
        }
    }

    public void obtainCape() {
        if (currentState.equals(State.SMALL) || currentState.equals(State.SUPER)) {
            this.currentState = State.CAPE;
            this.score += 200;
        }
    }

    public void obtainFireFlower() {
        if (currentState.equals(State.SMALL) || currentState.equals(State.SUPER)) {
            this.currentState = State.FIRE;
            this.score += 300;
        }
    }

    public void meetMonster() {
        if (currentState.equals(State.SUPER)) {
            this.currentState = State.SMALL;
            this.score -= 100;
            return;
        }

        if (currentState.equals(State.CAPE)) {
            this.currentState = State.SMALL;
            this.score -= 200;
            return;
        }

        if (currentState.equals(State.FIRE)) {
            this.currentState = State.SMALL;
            this.score -= 300;
            return;
        }
    }

    public int getScore() {
        return this.score;
    }

    public State getCurrentState() {
        return this.currentState;
    }
}
