package org.coder.concurrency.programming.pattern._7_context.memoryleak;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadLocal 伪内存泄露示例代码
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@WebServlet("/pseudo-leak")
public class MemoryPseudoLeakingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    final static ThreadLocal<AtomicInteger> TL_COUNTER = ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        PrintWriter pwr = resp.getWriter();
        pwr.write(String.valueOf(TL_COUNTER.get().getAndIncrement()));
        pwr.close();
    }

}