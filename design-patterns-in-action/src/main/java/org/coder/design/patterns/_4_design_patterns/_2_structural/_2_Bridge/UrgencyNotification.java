package org.coder.design.patterns._4_design_patterns._2_structural._2_Bridge;

import org.coder.design.patterns.simulate.Notification;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UrgencyNotification extends Notification {


    public UrgencyNotification(MsgSender msgSender) {
        super(msgSender);
    }

    @Override
    public void notify(String message) {
        msgSender.send(message);
    }
}
