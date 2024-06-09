package org.coder.design.patterns._2_design_principle._5_dip.di;

/**
 * 站内信发送类
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class InboxSender implements MessageSender {

    @Override
    public void send(String cellphone, String message) {
        // ...
    }
}
