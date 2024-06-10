package org.coder.design.patterns._2_design_principle._8_lod._1_case;

import org.coder.design.patterns._2_design_principle._8_lod._1_case.simulate.Html;
import org.coder.design.patterns._2_design_principle._8_lod._1_case.simulate.HtmlDownloader;

/**
 * 通过一个工厂方法来创建 Document
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class DocumentFactory {

    private HtmlDownloader downloader;

    public DocumentFactory(HtmlDownloader downloader) {
        this.downloader = downloader;
    }

    public Document createDocument(String url) {
        Html html = downloader.downloadHtml(url);
        return new Document(url, html);
    }
}
