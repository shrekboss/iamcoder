package org.coder.concurrency.programming.pattern._17_thread_pool.memoryleak;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/leak")
@SuppressWarnings("serial")
public class MemoryLeakingServlet extends HttpServlet {

    final static ThreadLocal<Counter> TL_COUNTER =
            new ThreadLocal<Counter>() {
                @Override
                protected Counter initialValue() {
                    return new Counter();
                }
            };

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