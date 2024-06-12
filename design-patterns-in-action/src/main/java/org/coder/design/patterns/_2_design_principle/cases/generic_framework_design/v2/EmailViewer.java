package org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v2;

import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v1.EmailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class EmailViewer implements StatViewer {

    private EmailSender emailSender;
    private List<String> toAddresses = new ArrayList<>();

    public EmailViewer() {
        this.emailSender = new EmailSender(/*省略参数*/);
    }

    public EmailViewer(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public EmailViewer(List<String> emailToAddresses) {
        this.toAddresses = emailToAddresses;
    }

    public void addToAddress(String address) {
        toAddresses.add(address);
    }

    public void output(Map requestStats, long startTimeInMillis, long endTimeInMills) {
        // format the requestStats to HTML style.
        // send it to email toAddresses.
    }
}
