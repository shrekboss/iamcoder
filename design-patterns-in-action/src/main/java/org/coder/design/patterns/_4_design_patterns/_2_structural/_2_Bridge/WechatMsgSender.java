package org.coder.design.patterns._4_design_patterns._2_structural._2_Bridge;

import java.util.List;

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
public class WechatMsgSender implements MsgSender {

    private List<String> wechatIds;

    public WechatMsgSender(List<String> wechatIds) {
        this.wechatIds = wechatIds;
    }

    @Override
    public void send(String message) {
        // ...
    }
}
