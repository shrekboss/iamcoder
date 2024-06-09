package org.coder.design.patterns._2_design_principle._2_ocp;

/**
 * 表示通知的紧急程度，包括 SEVERE（严重）、URGENCY（紧急）、NORMAL（普通）、TRIVIAL（无关紧要），不同的紧急程度对应不同的发送渠道。
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public enum NotificationEmergencyLevel {

    SEVERE,
    URGENCY,
    NORMAL,
    TRIVIAL
    ;
}
