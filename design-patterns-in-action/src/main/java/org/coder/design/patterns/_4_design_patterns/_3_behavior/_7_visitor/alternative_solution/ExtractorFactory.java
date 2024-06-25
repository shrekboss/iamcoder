package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.alternative_solution;

import java.util.HashMap;
import java.util.Map;

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
public class ExtractorFactory {
    private static final Map<ResourceFileType, Extractor> extractors = new HashMap<>();

    static {
        extractors.put(ResourceFileType.PDF, new PdfExtractor());
        extractors.put(ResourceFileType.PPT, new PPTExtractor());
        extractors.put(ResourceFileType.WORD, new WordExtractor());
    }

    public static Extractor getExtractor(ResourceFileType type) {
        return extractors.get(type);
    }
}
