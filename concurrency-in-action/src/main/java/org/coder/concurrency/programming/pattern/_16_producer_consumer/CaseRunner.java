package org.coder.concurrency.programming.pattern._16_producer_consumer;

import org.coder.concurrency.programming.util.Debug;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CaseRunner {

    public static void main(String[] args) {
        AttachmentProcessor ap = new AttachmentProcessor();
        ap.init();
        InputStream in = new ByteArrayInputStream("Hello".getBytes());
        try {
            ap.saveAttachment(in, "000887282", "测试文档");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.info("File stored at:\n"
                + AttachmentProcessor.ATTACHMENT_STORE_BASE_DIR);
    }

}