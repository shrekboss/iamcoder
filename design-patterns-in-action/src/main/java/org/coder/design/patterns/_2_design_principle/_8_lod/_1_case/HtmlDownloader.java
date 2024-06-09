package org.coder.design.patterns._2_design_principle._8_lod._1_case;

import org.coder.design.patterns._2_design_principle._8_lod._1_case.mock.Html;
import org.coder.design.patterns._2_design_principle._8_lod._1_case.mock.HtmlRequest;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class HtmlDownloader {
    private NetworkTransporter transporter;//通过构造函数或IOC注入

    // HtmlDownloader这里也要有相应的修改
    public Html downloadHtml(String url) {
        HtmlRequest htmlRequest = new HtmlRequest(url);
        Byte[] rawHtml = transporter.send(
                htmlRequest.getAddress(), htmlRequest.getContent().getBytes());
        return new Html(rawHtml);
    }
}