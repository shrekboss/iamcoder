package org.coder.concurrency.programming.pattern._7_context.memoryleak;

import org.coder.concurrency.programming.pattern._7_context.ManagedThreadLocal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 使用 ManagedThreadLocal 类防止 ThreadLocal 内存泄露
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@WebServlet("/noleak")
@WebListener
@SuppressWarnings("serial")
public class MemoryLeakPreventingServlet extends HttpServlet implements ServletContextListener {

    private final static ManagedThreadLocal<Counter> TL_COUNTER = new ManagedThreadLocal<Counter>() {
        @Override
        protected Counter initialValue() {
            return new Counter();
        }
    };

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        TL_COUNTER.destroy();

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // 什么也不做
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain;charset=utf-8");
        PrintWriter pwr = resp.getWriter();
        pwr.write(String.valueOf(TL_COUNTER.get().getAndIncrement()));
        pwr.flush();
        pwr.close();
    }
}