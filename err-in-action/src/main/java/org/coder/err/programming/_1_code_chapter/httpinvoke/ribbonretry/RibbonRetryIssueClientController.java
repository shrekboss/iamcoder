package org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry;

import lombok.extern.slf4j.Slf4j;
import org.coder.err.programming._1_code_chapter.httpinvoke.ribbonretry.feign.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("ribbonretryissueclient")
@Slf4j
public class RibbonRetryIssueClientController {

    @Autowired
    private SmsClient smsClient;

    /**
     * curl http://localhost:45678/ribbonretryissueclient/wrong
     */
    @GetMapping("wrong")
    public String wrong() {
        log.info("client is called");
        try {
            smsClient.sendSmsWrong("13600000000", UUID.randomUUID().toString());
            // smsClient.sendSmsRight("13600000000", UUID.randomUUID().toString());
        } catch (Exception ex) {
            log.error("send sms failed : {}", ex.getMessage());
        }
        return "done";
    }
}
