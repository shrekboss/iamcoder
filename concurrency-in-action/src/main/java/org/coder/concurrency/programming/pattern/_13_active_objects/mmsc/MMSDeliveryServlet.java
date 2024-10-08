package org.coder.concurrency.programming.pattern._13_active_objects.mmsc;

import org.coder.concurrency.programming.util.Debug;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class MMSDeliveryServlet extends HttpServlet {

    private static final long serialVersionUID = -4246026878037616781L;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /*
         * 将请求中的数据解析为内部对象。
         */
        MMSDeliverRequest mmsDeliverReq =
                this.parseRequest(req.getInputStream());
        Recipient shortNumberRecipient = mmsDeliverReq.getRecipient();
        Recipient originalNumberRecipient = null;

        try {
            // 将接收方短号转换为长号
            originalNumberRecipient = convertShortNumber(shortNumberRecipient);
        } catch (SQLException e) {

            // 接收方短号转换为长号时发生数据库异常，触发请求消息的缓存
            AsyncRequestPersistence.getInstance().store(mmsDeliverReq);

            // 省略其他代码

            resp.setStatus(202);
        }

        Debug.info(String.valueOf(originalNumberRecipient));
    }

    private MMSDeliverRequest parseRequest(InputStream reqInputStream) {
        MMSDeliverRequest mmsDeliverReq = new MMSDeliverRequest();
        // 省略其他代码
        return mmsDeliverReq;
    }

    private Recipient convertShortNumber(Recipient shortNumberRecipient)
            throws SQLException {
        Recipient recipent = null;
        // 省略其他代码
        return recipent;
    }

}