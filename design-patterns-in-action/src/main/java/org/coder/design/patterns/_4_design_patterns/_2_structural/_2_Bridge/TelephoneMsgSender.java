package org.coder.design.patterns._4_design_patterns._2_structural._2_Bridge;

import java.util.List;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TelephoneMsgSender implements MsgSender {

    private List<String> telephones;

    public TelephoneMsgSender(List<String> telephones) {
        this.telephones = telephones;
    }

    @Override
    public void send(String message) {
        // ...
    }
}
