package org.coder.design.patterns._4_design_patterns._3_behavior._5_state.state_model;

import org.coder.design.patterns._4_design_patterns._3_behavior._5_state.State;

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
public interface IMario { //所有状态类的接口
    State getName();

    //以下是定义的事件
    void obtainMushRoom(MarioStateMachine stateMachine);

    void obtainCape(MarioStateMachine stateMachine);

    void obtainFireFlower(MarioStateMachine stateMachine);

    void meetMonster(MarioStateMachine stateMachine);
}
