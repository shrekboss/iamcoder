package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor;

import java.util.ArrayList;
import java.util.List;

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
public class ToolApplication {

    /**
     * 运行结果是：
     * Extract PDF.
     * Extract WORD.
     * Extract PPT.
     */
    public static void main(String[] args) {
        String resourceDirectory = args.length != 0 ? args[0] : "123";
        List<ResourceFile> resourceFiles = listAllResourceFiles(resourceDirectory);

        Extractor extractor = new Extractor();
        for (ResourceFile resourceFile : resourceFiles) {
            // 根据多态特性，程序会调用实际类型的 accept 函数，比如 PdfFile 的 accept 函数，也就是第 20 行代码。
            // 而 20 行代码中的 this 类型是 PdfFile 的，在编译的时候就确定了，所以会调用 extractor 的 extract2txt(PdfFile pdfFile)
            // 这个重载函数
            resourceFile.accept(extractor);
        }

        Compressor compressor = new Compressor();
        for (ResourceFile resourceFile : resourceFiles) {
            resourceFile.accept(compressor);
        }
    }

    private static List<ResourceFile> listAllResourceFiles(String resourceDirectory) {
        List<ResourceFile> resourceFiles = new ArrayList<>();
        //...根据后缀(pdf/ppt/word)由工厂方法创建不同的类对象(PdfFile/PPTFile/WordFile)
        resourceFiles.add(new PdfFile("a.pdf"));
        resourceFiles.add(new WordFile("b.word"));
        resourceFiles.add(new PPTFile("c.ppt"));
        return resourceFiles;
    }

}