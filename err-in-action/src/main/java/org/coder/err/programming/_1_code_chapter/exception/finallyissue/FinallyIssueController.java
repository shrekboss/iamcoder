package org.coder.err.programming._1_code_chapter.exception.finallyissue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("finallyissue")
public class FinallyIssueController {

    /**
     * curl http://localhost:45678/finallyissue/useresourcewrong
     */
    @GetMapping("useresourcewrong")
    public void useresourcewrong() throws Exception {
        TestResource testResource = new TestResource();
        try {
            //异常丢失
            testResource.read();
        } finally {
            testResource.close();
        }
    }

    /**
     * curl http://localhost:45678/finallyissue/useresourceright
     */
    @GetMapping("useresourceright")
    public void useresourceright() throws Exception {
        try (TestResource testResource = new TestResource()) {
            testResource.read();
        }
    }

    /**
     * curl http://localhost:45678/finallyissue/wrong
     */
    @GetMapping("wrong")
    public void wrong() {
        try {
            log.info("try");
            //异常丢失
            throw new RuntimeException("try");
        } finally {
            log.info("finally");
            throw new RuntimeException("finally");
        }
    }

    /**
     * curl http://localhost:45678/finallyissue/right
     */
    @GetMapping("right")
    public void right() {
        try {
            log.info("try");
            throw new RuntimeException("try");
        } finally {
            log.info("finally");
            try {
                // finally 代码块自己负责异常捕获和处理
                throw new RuntimeException("finally");
            } catch (Exception ex) {
                log.error("finally", ex);
            }
        }
    }

    /**
     * curl http://localhost:45678/finallyissue/right2
     */
    @GetMapping("right2")
    public void right2() throws Exception {
        Exception e = null;
        try {
            log.info("try");
            throw new RuntimeException("try");
        } catch (Exception ex) {
            e = ex;
        } finally {
            log.info("finally");
            try {
                throw new RuntimeException("finally");
            } catch (Exception ex) {
                if (e != null) {
                    // 把 try 中的异常作为主异常抛出，使用 addSuppressed 方法把 finally 中的异常附加到主异常上
                    e.addSuppressed(ex);
                } else {
                    e = ex;
                }
            }
        }
        throw e;
    }
}
